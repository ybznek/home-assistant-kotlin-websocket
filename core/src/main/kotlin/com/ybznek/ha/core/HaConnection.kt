package com.ybznek.ha.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import java.util.concurrent.atomic.AtomicBoolean

typealias TypedTree = Pair<String, JsonNode>

/**
 * Manages WebSocket connection to HomeAssistant
 */
class HaConnection(
    val host: String,
    val port: Int = 80,
    val path: String = "/api/websocket"
) : AutoCloseable {

    private lateinit var web: DefaultClientWebSocketSession
    private val _closed = AtomicBoolean(false)
    val closed get() = _closed.get()

    private val client = HttpClient {
        install(WebSockets) {
            pingIntervalMillis = 20_000
        }
    }

    @PublishedApi
    internal val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .registerModules(JavaTimeModule())
        .registerModules(AfterburnerModule())
        .registerKotlinModule()

    suspend fun start() =
        flow<Frame> { processWebSocket(this) }
            .map { mapper.readTree(it.data) }
            .transform<JsonNode, TypedTree> {
                processTree(it)
            }

    private suspend fun processWebSocket(flowCollector: FlowCollector<Frame>) {
        client.webSocket(method = HttpMethod.Get, host = host, port = port, path = path) {
            web = this
            while (!closed) {
                val frame = incoming.receive()
                flowCollector.emit(frame)
            }
        }
    }

    private suspend fun FlowCollector<TypedTree>.processTree(tree: JsonNode) {
        when (tree) {
            is ObjectNode -> emit(processTreeInternal(tree))
            is ArrayNode -> tree.forEach {
                emit(processTreeInternal(it))
            }
            else -> error("unexpected type: $tree")
        }
    }

    private fun processTreeInternal(tree: JsonNode): TypedTree {
        val jsonNode = tree["type"] ?: error(tree.toPrettyString())
        val type = jsonNode.textValue().toString()
        return TypedTree(type, tree)
    }

    @PublishedApi
    internal suspend fun send(message: String) {
        web.send(Frame.Text(message))
        // TODO handle exception // block if cannot be sent
    }

    suspend fun send(message: Any) = send(mapper.writeValueAsString(message))

    inline fun <reified T : Any> parseTree(tree: JsonNode): T =
        mapper.treeToValue<T>(tree)

    fun <T : Any> parseTree(tree: JsonNode, type: TypeReference<T>): T =
        mapper.treeToValue(tree, mapper.constructType(type))

    override fun close() {
        client.close()
        _closed.set(true)
    }
}