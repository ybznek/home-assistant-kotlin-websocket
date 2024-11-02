package com.ybznek.ha.core.data

import java.time.Instant

data class UnitSystem(
    val length: String,
    val mass: String,
    val volume: String,
    val temperature: String,
    val pressure: String,
    val windSpeed: String,
    val accumulatedPrecipitation: String,
)

data class HassEntity(
    val entityId: String,
    val state: String,
    val lastChanged: Instant,
    val lastUpdated: Instant,
    val attributes: Map<String, Any>,
    val context: Context,
)

