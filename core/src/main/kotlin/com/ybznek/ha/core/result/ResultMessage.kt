package com.ybznek.ha.core.result

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.ybznek.ha.core.HassUser
import com.ybznek.ha.core.data.Context
import com.ybznek.ha.core.data.HassEntity

data class CallServiceResultMessage(val context: Context)

typealias ResultMessageCallService = ResultMessage<CallServiceResultMessage>
typealias ResultMessageGetStates = ResultMessage<List<HassEntity>>
typealias ResultMessageGetConfig = ResultMessage<HassConfig>
typealias ResultMessageGetServices = ResultMessage<Map<String, Map<String, HassService>>>
typealias ResultMessageGetUser = ResultMessage<HassUser>

sealed interface Msg<V> {
    val raw: JsonNode
    val parsed: V
}

internal data class RawMsg<V>(
    override val raw: JsonNode,
    override val parsed: V
) : Msg<V> {
    override fun toString(): String = "Msg(raw=$raw, parsed=$parsed)"
}


internal data class LazyProvider<T>(
    private val type: TypeReference<T>,
    private val mapper: ObjectMapper
) {
    private var inner: Any? = Companion

    private companion object

    @Suppress("UNCHECKED_CAST")
    fun get(tree: JsonNode) = when {
        inner === Companion -> parse(tree).also { inner = it }
        else -> inner as T
    }

    private fun parse(tree: JsonNode): T = try {
        mapper.treeToValue(tree, type)
    } catch (e: JacksonException) {
        println(tree)
        throw IllegalArgumentException("Unable to parse response message", e)
    }
}

internal data class LazyMsg<V>(
    override val raw: JsonNode,
    private val provider: LazyProvider<V>,
) : Msg<V> {
    override val parsed get() = provider.get(raw)

    override fun toString(): String = "Msg(raw=$raw, parsed=$parsed)"
}

data class ResultMessage<T>(
    override val id: Int,
    override val type: String,
    val success: Boolean,
    val result: T?,
) : ServerMessage