package com.ybznek.ha.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.ybznek.ha.core.MessageType.DefaultMessageType
import com.ybznek.ha.core.data.AuthInvalidMessage
import com.ybznek.ha.core.data.ServerTypes
import com.ybznek.ha.core.dispatcher.ResponseDispatcher
import com.ybznek.ha.core.result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

private typealias MessageToSend = Map<String, Any?>

data class HassUser(
    val id: String,
    val isAdmin: Boolean,
    val isOwner: Boolean,
    val name: String
)


abstract class HaClientBase(
    host: String,
    port: Int,
    path: String,
    private val token: String
) : AutoCloseable {
    private val getId: () -> Int = AtomicInteger(0)::incrementAndGet
    private val responses = ResponseDispatcher<Int, JsonNode>()
    private var _version: String? = null
    val version: String? get() = _version
    protected val coroutineScope = CoroutineScope(Dispatchers.Default)

    val MessageToSend.id get() = this["id"] as Int
    val MessageToSend.type get() = this["type"] as String

    @PublishedApi
    internal val conn = HaConnection(host, port, path)

    suspend fun start() {
        while (!conn.closed) {
            try {
                conn
                    .start()
                    .collect { (type, tree) ->
                        coroutineScope.launch {
                            try {
                                processMessage(type, tree)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun subscribeEvent(eventType: String): Int {
        return buildMessage(
            DefaultMessageType.SUBSCRIBE_EVENTS,
            "event_type" to eventType
        ).also { conn.send(it) }.id
    }

    suspend fun callService(
        domain: String,
        service: String,
        data: Map<String, Any?> = emptyMap()
    ): Msg<ResultMessageCallService> {
        val msg = buildMessage(
            type = DefaultMessageType.CALL_SERVICE,
            "domain" to domain,
            "service" to service
        ) + data

        return sendSuspendAndParse(msg, jacksonTypeRef<ResultMessageCallService>())
    }

    suspend fun getStates(): Msg<ResultMessageGetStates> =
        standardMessage(DefaultMessageType.GET_STATES, typeReference = jacksonTypeRef())

    private suspend fun supportedFeatures(): Msg<Map<*, *>> =
        standardMessage(
            DefaultMessageType.SUPPORTED_FEATURES,
            jacksonTypeRef(),
            "features" to mapOf("coalesce_messages" to 1)
        )

    suspend fun getServices(): Msg<ResultMessageGetServices> =
        standardMessage(DefaultMessageType.GET_SERVICES, jacksonTypeRef())

    suspend fun getUser(): Msg<ResultMessageGetUser> =
        standardMessage(DefaultMessageType.AUTH_CURRENT_USER, jacksonTypeRef())

    suspend fun getConfig(): Msg<ResultMessageGetConfig> =
        standardMessage(DefaultMessageType.GET_CONFIG, typeReference = jacksonTypeRef())

    private suspend fun <T : Any> standardMessage(
        type: DefaultMessageType,
        typeReference: TypeReference<T>,
        vararg values: Pair<String, Any?>,
    ) = sendSuspendAndParse(buildMessage(type, *values), typeReference)

    fun buildMessage(
        type: MessageType,
        vararg values: Pair<String, Any?>
    ): MessageToSend =
        buildMap(2 + values.size) {
            put("id", getId())
            put("type", type.value)
            putAll(values)
        }

    @PublishedApi
    internal suspend fun sendSuspend(msg: MessageToSend) =
        responses.waitForResponse(msg.id) { conn.send(msg) }


    suspend fun <T : Any> HaClientBase.sendSuspendAndParse(
        msg: MessageToSend,
        type: TypeReference<T>
    ): Msg<T> {
        val tree = sendSuspend(msg)
        return LazyMsg(raw = tree, LazyProvider(type, conn.mapper))
    }

    private suspend fun processMessage(type: String, tree: JsonNode) {
        when (type) {
            ServerTypes.AUTH_REQUIRED -> onAuthRequired(tree)
            ServerTypes.AUTH_OK -> onAuthOk(tree)
            ServerTypes.PONG -> onPong(tree)
            ServerTypes.AUTH_INVALID -> onAuthInvalid(tree)
            ServerTypes.RESULT -> onResult(tree)
            ServerTypes.EVENT -> onEvent(tree)
            else -> onUnexpectedType(type, tree)
        }
    }

    protected open suspend fun onUnexpectedType(type: String, tree: JsonNode) {
        println("Unexpected type $type")
    }

    protected open suspend fun onEvent(tree: JsonNode) {}

    protected open suspend fun onResult(tree: JsonNode) {
        responses.send(tree["id"].intValue(), tree)
    }

    protected open suspend fun onAuthInvalid(tree: JsonNode) {
        error(conn.parseTree<AuthInvalidMessage>(tree))
    }

    protected open suspend fun onPong(tree: JsonNode) {
        conn.parseTree<ServerMessage>(tree)
    }

    protected open suspend fun onAuthOk(tree: JsonNode) {
        data class AuthOk(val type: String, val haVersion: String)

        val parsed = conn.parseTree<AuthOk>(tree)
        _version = parsed.haVersion

        supportedFeatures()
        subscribeEvent("state_changed")
        onInitialState(getStates())
    }

    protected open suspend fun onAuthRequired(tree: JsonNode) {
        val message = mapOf(
            "type" to "auth",
            "access_token" to token
        )
        conn.send(message)
    }

    abstract suspend fun onInitialState(message: Msg<ResultMessageGetStates>)

    override fun close() {
        conn.close()
        coroutineScope.cancel()
    }
}