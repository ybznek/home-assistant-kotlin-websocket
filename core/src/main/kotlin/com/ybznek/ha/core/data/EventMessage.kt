package com.ybznek.ha.core.data

import java.time.Instant

data class EventMessage(
    val eventType: String,
    val data: EventData,
    val origin: String,
    val timeFired: Instant,
    val context: Context
)