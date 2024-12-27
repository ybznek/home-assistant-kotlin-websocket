package com.ybznek.ha.core

import com.fasterxml.jackson.databind.JsonNode
import com.ybznek.ha.core.data.EntityState
import com.ybznek.ha.core.data.State
import com.ybznek.ha.core.dispatcher.Dispatcher
import com.ybznek.ha.core.dispatcher.StateChanged
import com.ybznek.ha.core.dispatcher.TriggerableDispatcher
import com.ybznek.ha.core.result.Msg
import com.ybznek.ha.core.result.RawMsg
import com.ybznek.ha.core.result.ResultMessageGetStates
import com.ybznek.ha.core.result.SubscriptionMessage
import com.ybznek.ha.core.state.StateHolder
import com.ybznek.ha.core.state.StateProvider
import com.ybznek.ha.core.util.KeyOptimizer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class HaClient(
    host: String,
    port: Int,
    path: String,
    token: String,
    private val clock: Clock = Clock.System,
) : HaClientBase(host, port, path, token) {
    private val map: MutableMap<EntityIdString, StateHolder<TypedEntity>> = ConcurrentHashMap()
    private val triggerableDispatcher = TriggerableDispatcher<HaClient, StateChanged<TypedEntity>>()
    private val optimizeKey = KeyOptimizer()

    val states: Map<EntityIdString, StateProvider<TypedEntity>> = map
    val changeListener: Dispatcher<HaClient, StateChanged<TypedEntity>> = triggerableDispatcher
    private val channel = Channel<Unit>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override suspend fun onEvent(tree: JsonNode) {
        val message = conn.parseTree<SubscriptionMessage>(tree)
        val event = message.event
        val data = event.data
        val newState = data.newState?.toEntityState()
        val timeFired = event.timeFired.toKotlinInstant()
        val entityId = optimizeKey(data.entityId)

        putData(
            entityId = entityId,
            timeFired = timeFired,
            state = newState
        )

        if (triggerableDispatcher.anyListener) {
            triggerChange(entityId, RawMsg(tree, message), timeFired, data.oldState?.toEntityState(), newState)
        }
    }

    private suspend fun triggerChange(
        entity: EntityIdString,
        msg: Msg<*>,
        time: Instant,
        oldState: EntityState<TypedEntity>?,
        newState: EntityState<TypedEntity>?
    ) {
        val changed = StateChanged(
            entity = EntityId(entity),
            time = time,
            raw = msg,
            oldState = oldState,
            newState = newState
        )
        triggerableDispatcher.trigger(this, changed)
    }

    private fun State.toEntityState() =
        EntityState<TypedEntity>(
            state = this.state,
            attributes = optimizeAttributes(this.attributes),
            lastChanged = this.lastChanged.toKotlinInstant(),
            lastUpdated = this.lastUpdated.toKotlinInstant(),
            lastReported = this.lastReported.toKotlinInstant(),
            context = this.context
        )

    private fun putData(
        entityId: EntityIdString,
        timeFired: Instant,
        state: EntityState<TypedEntity>?
    ) {
        when (val holder = map[entityId]) {
            null -> map.computeIfAbsent(entityId) { _ -> StateHolder(timeFired, AtomicReference(state)) }
            else -> if (holder.time <= timeFired) holder.stateRef.set(state)
        }
    }

    override suspend fun onInitialState(message: Msg<ResultMessageGetStates>) {
        val now = clock.now()

        for (res in message.parsed.result ?: return) {
            val entityId = optimizeKey(res.entityId)
            val state = EntityState<TypedEntity>(
                state = res.state,
                attributes = optimizeAttributes(res.attributes),
                lastChanged = res.lastChanged.toKotlinInstant(),
                lastReported = now,
                lastUpdated = res.lastUpdated.toKotlinInstant(),
                context = res.context
            )

            putData(
                entityId = entityId,
                timeFired = now,
                state = state
            )

            if (changeListener.anyListener) {
                triggerChange(entityId, message, now, oldState = null, newState = state)
            }
        }
        channel.send(Unit)
    }

    suspend fun waitForStart() {
        channel.receive()
        channel.send(Unit)
    }

    private fun optimizeAttributes(attributes: Map<String, Any?>): Map<String, Any?> =
        HashMap<String, Any?>(attributes.size).also {
            for ((key, value) in attributes) {
                it[optimizeKey(key)] = value
            }
        }

    override fun close() {
        super.close()
        triggerableDispatcher.close()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : TypedEntity> getState(id: EntityId<T>): StateProvider<T> = states[id.entityId] as StateProvider<T>
}