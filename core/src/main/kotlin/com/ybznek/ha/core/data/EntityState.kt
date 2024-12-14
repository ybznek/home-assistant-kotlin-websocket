package com.ybznek.ha.core.data

import com.ybznek.ha.core.Attributes
import com.ybznek.ha.core.TypedEntity
import kotlinx.datetime.Instant

data class EntityState<T : TypedEntity>(
    val state: String,
    val attributes: Attributes,
    val lastChanged: Instant,
    val lastUpdated: Instant,
    val lastReported: Instant,
    val context: Context
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> getAttribute(key: String) = attributes[key] as T
}