package com.ybznek.ha.core.data

import java.time.Instant

interface EventBase {
    val origin: String
    val timeFired: Instant
    val context: Context
}

interface HassEvent : EventBase {
    val eventType: String
    val data: Any
}

data class StateChangedEvent(
    override val eventType: String,
    override val data: EventData,
    override val origin: String,
    override val timeFired: Instant,
    override val context: Context
) : HassEvent