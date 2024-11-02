package com.ybznek.ha.core

interface MessageType {
    val value: String

    enum class DefaultMessageType(override val value: String) : MessageType {
        AUTH("auth"),
        SUPPORTED_FEATURE("supported_feature"),
        GET_STATES("get_states"),
        GET_CONFIG("get_config"),
        GET_SERVICES("get_services"),
        AUTH_CURRENT_USER("auth/current_user"),
        CALL_SERVICE("call_service"),
        SUBSCRIBE_EVENTS("subscribe_events"),
        UNSUBSCRIBE_EVENTS("unsubscribe_events"),
        PING("ping")
    }
}