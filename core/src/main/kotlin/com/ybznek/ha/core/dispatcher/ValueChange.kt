package com.ybznek.ha.core.dispatcher

data class ValueChange<T>(val from: T, val to: T) {
    override fun toString() = "Change($from => $to)"
}