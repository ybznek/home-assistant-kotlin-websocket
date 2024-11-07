package com.ybznek.ha.core.dispatcher

interface Dispatcher<TOwner, TMessage> {
    val anyListener: Boolean
    operator fun plusAssign(handler: suspend (TOwner, TMessage) -> Unit)
    operator fun minusAssign(handler: suspend (TOwner, TMessage) -> Unit)
}