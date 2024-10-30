package com.ybznek.ha.core.state

import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.data.EntityState
import kotlinx.datetime.Instant
import java.util.concurrent.atomic.AtomicReference

internal data class StateHolder<T : TypedEntity>(
    val time: Instant,
    val stateRef: AtomicReference<EntityState<T>>
) : StateProvider<T> {
    override val state: EntityState<T> get() = stateRef.get()
}