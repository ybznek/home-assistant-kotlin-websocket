package com.ybznek.ha.core.dispatcher

interface Dispatcher<TOwner, TMessage> {
    val anyListener: Boolean
    operator fun plusAssign(handler: (TOwner, TMessage) -> Unit)
    operator fun minusAssign(handler: (TOwner, TMessage) -> Unit)
}