package com.ybznek.ha.core.util

import io.ktor.util.collections.*

internal data class KeyOptimizer(
    private val keyMap: MutableMap<String, String> = ConcurrentMap()
) {
    operator fun invoke(key: String) = keyMap.computeIfAbsent(key) { it }
}