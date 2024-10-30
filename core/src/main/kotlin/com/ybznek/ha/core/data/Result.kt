package com.ybznek.ha.core.data

import com.ybznek.ha.core.Attributes

data class Result(
    val entityId: String,
    val context: Context,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val elevation: Int = 0,
    val unitSystem: Map<String, Any?>?,
    val locationName: String?,
    val timeZone: String?,
    val components: List<String>?,
    val configDir: String?,
    val whitelistExternalDirs: List<String>?,
    val allowlistExternalDirs: List<String>?,
    val allowlistExternalUrls: List<String>?,
    val version: String?,
    val configSource: String?,
    val isSafeMode: Boolean = false,
    val state: String,
    val externalUrl: String?,
    val internalUrl: String?,
    val attributes: Attributes,
    val lastChanged: java.time.Instant,
    val lastUpdated: java.time.Instant
)