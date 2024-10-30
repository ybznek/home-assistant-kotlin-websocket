package com.ybznek.ha.core.dispatcher

import kotlinx.coroutines.channels.Channel
import java.util.concurrent.ConcurrentHashMap


internal class ResponseDispatcher<TId, TResp> {

    private val responseChannels: MutableMap<TId, Channel<TResp>> = ConcurrentHashMap()

    suspend fun send(id: TId, data: TResp) {
        responseChannels[id]?.send(data)
    }

    suspend fun waitForResponse(requestId: TId, send: suspend () -> Unit): TResp {
        val channel = Channel<TResp>(1)
        responseChannels[requestId] = channel
        return try {
            send()
            channel.receive()
        } finally {
            responseChannels.remove(requestId)
            channel.cancel()
        }
    }
}