package com.ybznek.ha.core.result

data class ServiceDescription(
    val name: String?,
    val description: String?,
    val fields: Map<String, Any?>?,
    val target: Map<String, Any?>?,
)