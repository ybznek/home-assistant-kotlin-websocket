package com.ybznek.ha.core.dispatcher

import kotlinx.coroutines.*
import java.util.*

class TriggerableDispatcher<TOwner, TMessage>(
    private val dispatcher: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val listeners: MutableList<(TOwner, TMessage) -> Unit> = Collections.synchronizedList(ArrayList())
) : Dispatcher<TOwner, TMessage>, AutoCloseable {

    override val anyListener: Boolean
        get() = listeners.isNotEmpty()

    override operator fun plusAssign(handler: (TOwner, TMessage) -> Unit) {
        listeners += handler
    }

    override operator fun minusAssign(handler: (TOwner, TMessage) -> Unit) {
        listeners.remove(handler)
    }

    suspend fun trigger(owner: TOwner, event: TMessage) {
        if (listeners.isEmpty())
            return
        listeners.map { dispatcher.launch { runBlocking { it(owner, event) } } }.joinAll()
    }

    override fun close() = dispatcher.cancel()
}