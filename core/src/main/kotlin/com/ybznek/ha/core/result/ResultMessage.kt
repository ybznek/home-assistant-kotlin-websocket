package com.ybznek.ha.core.result

import com.fasterxml.jackson.databind.JsonNode
import com.ybznek.ha.core.HassUser
import com.ybznek.ha.core.data.HassEntity

typealias ResultMessageCallService = ResultMessage<JsonNode>
typealias ResultMessageGetStates = ResultMessage<List<HassEntity>>
typealias ResultMessageGetConfig = ResultMessage<HassConfig>
typealias ResultMessageGetServices = ResultMessage<Map<String, Map<String, HassService>>>
typealias ResultMessageGetUser = ResultMessage<HassUser>

data class Msg<V>(val raw: JsonNode, val parsed: V)

data class ResultMessage<T>(
    override val id: Int,
    override val type: String,
    val success: Boolean,
    val result: T?,
) : ServerMessage