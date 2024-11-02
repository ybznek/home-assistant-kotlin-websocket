package com.ybznek.ha.core.result

import com.ybznek.ha.core.HassServiceTarget
import com.ybznek.ha.core.data.UnitSystem

data class HassResponse(
    val optional: Boolean
)

data class HassService(
    val name: String?,
    val description: String?,
    val target: HassServiceTarget,
    val fields: Map<String, HassField>,
    val response: HassResponse
)

data class HassFilter(
    val supportedFeatures: List<Long>?,
    val attribute: Map<String, List<Any?>>?
)

data class HassField(
    val example: Any?,
    val default: Any?,
    val required: Boolean?,
    val advanced: Boolean?,
    val selector: Any,
    val filter: HassFilter?,
    val name: String?,
    val description: String,
)

enum class HassState { NOT_RUNNING, STARTING, RUNNING, STOPPING, FINAL_WRITE }

data class HassConfig(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val radius: Double,
    val unitSystem: UnitSystem,
    val locationName: String,
    val timeZone: String,
    val components: List<String>,
    val configDir: String,
    val allowlistExternalDirs: List<String>,
    val allowlistExternalUrls: List<String>,
    val version: String,
    val configSource: String,
    val recoveryMode: Boolean,
    val safeMode: Boolean,
    val state: HassState,

    val externalUrl: String?,
    val internalUrl: String?,
    val currency: String,
    val country: String?,
    val language: String
)