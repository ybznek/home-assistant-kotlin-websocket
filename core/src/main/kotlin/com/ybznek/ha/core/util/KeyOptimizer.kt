package com.ybznek.ha.core.util

internal data class KeyOptimizer(
    private val keyMap: MutableMap<String, String> = HashMap()
) {
    operator fun invoke(key: String) = keyMap.computeIfAbsent(key) { it }
}