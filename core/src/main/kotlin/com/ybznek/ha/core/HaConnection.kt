package com.ybznek.ha.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Manages WebSocket connection to HomeAssistant
 */
class HaConnection(
    val host: String,
    val port: Int = 80,
    val path: String = "/api/websocket",
    val processMessage: suspend (type: String, tree: JsonNode) -> Unit
) : AutoCloseable {

    private lateinit var web: DefaultClientWebSocketSession
    private val closed = AtomicBoolean(false)
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
        .registerKotlinModule()

    suspend fun start() =
        client.webSocket(method = HttpMethod.Get, host = host, port = port, path = path) {
            web = this
            while (!closed.get()) {
                val receive = incoming.receive()
                val data = receive.data
                val tree = mapper.readTree(data)
                processTree(tree)
            }
        }

    private suspend fun processTree(tree: JsonNode) {
        when (tree) {
            is ArrayNode -> {
                for (item in tree) {
                    processTree(item)
                }
            }

            is ObjectNode -> {
                val jsonNode = tree["type"]
                    ?: error(tree.toPrettyString())
                val type = jsonNode.textValue().toString()
                processMessage(type, tree)
            }

            else -> error("unexpected type: $tree")
        }
    }

    @PublishedApi
    internal suspend fun send(message: String) = web.send(message)

    suspend fun send(message: Any) = send(mapper.writeValueAsString(message))

    inline fun <reified T : Any> parseTree(tree: JsonNode): T =
        mapper.treeToValue<T>(tree)

    fun <T : Any> parseTree(tree: JsonNode, type: TypeReference<T>): T =
        mapper.treeToValue(tree, mapper.constructType(type))

    override fun close() {
        client.close()
        closed.set(true)
    }
}