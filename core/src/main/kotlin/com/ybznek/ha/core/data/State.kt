package com.ybznek.ha.core.data

import com.ybznek.ha.core.Attributes
import java.time.Instant

data class State(
    val entityId: String,
    val state: String,
    val attributes: Attributes,
    val lastChanged: Instant,
    val lastUpdated: Instant,
    val lastReported: Instant,
    val context: Context
)