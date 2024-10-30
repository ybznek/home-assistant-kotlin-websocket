package com.ybznek.ha.core

import com.fasterxml.jackson.databind.JsonNode
import com.ybznek.ha.core.data.EntityState
import com.ybznek.ha.core.data.State
import com.ybznek.ha.core.dispatcher.Dispatcher
import com.ybznek.ha.core.dispatcher.StateChanged
import com.ybznek.ha.core.dispatcher.TriggerableDispatcher
import com.ybznek.ha.core.result.Msg
import com.ybznek.ha.core.result.ResultMessageGetStates
import com.ybznek.ha.core.result.SubscriptionMessage
import com.ybznek.ha.core.state.StateHolder
import com.ybznek.ha.core.state.StateProvider
import com.ybznek.ha.core.util.KeyOptimizer
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

    override suspend fun onEvent(tree: JsonNode) {
        val message = conn.parseTree<SubscriptionMessage>(tree)
        val event = message.event
        val data = event.data
        val newState = toEntityState(data.newState)
        val timeFired = event.timeFired.toKotlinInstant()

        putData(
            entityId = data.entityId,
            timeFired = timeFired,
            state = newState
        )

        if (triggerableDispatcher.anyListener) {
            triggerChange(data.entityId, Msg(tree, message), timeFired, toEntityState(data.oldState), newState)
        }
    }

    private suspend fun triggerChange(
        entity: EntityIdString,
        msg: Msg<*>,
        time: Instant,
        oldState: EntityState<TypedEntity>?,
        newState: EntityState<TypedEntity>
    ) {
        val changed = StateChanged(
            entity = entity,
            time = time,
            raw = msg,
            oldState = oldState,
            newState = newState
        )
        triggerableDispatcher.trigger(this, changed)
    }

    private fun toEntityState(state: State) =
        EntityState<TypedEntity>(
            state = state.state,
            attributes = optimizeAttributes(state.attributes),
            lastChanged = state.lastChanged.toKotlinInstant(),
            lastUpdated = state.lastUpdated.toKotlinInstant(),
            lastReported = state.lastReported.toKotlinInstant(),
            context = state.context
        )

    private fun putData(
        entityId: EntityIdString,
        timeFired: Instant,
        state: EntityState<TypedEntity>
    ) {
        val holder = map[entityId]
            ?: map.computeIfAbsent(entityId) { _ -> StateHolder(timeFired, AtomicReference(state)) }

        if (holder.time <= timeFired) {
            holder.stateRef.set(state)
        }
    }

    override suspend fun onInitialState(message: Msg<ResultMessageGetStates>) {
        val now = clock.now()

        for (res in message.parsed.result ?: return) {
            val state = EntityState<TypedEntity>(
                state = res.state,
                attributes = optimizeAttributes(res.attributes),
                lastChanged = res.lastChanged.toKotlinInstant(),
                lastReported = now,
                lastUpdated = res.lastUpdated.toKotlinInstant(),
                context = res.context
            )

            putData(
                entityId = res.entityId,
                timeFired = now,
                state = state
            )

            if (changeListener.anyListener) {
                triggerChange(res.entityId, message, now, oldState = null, newState = state)
            }
        }
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
}