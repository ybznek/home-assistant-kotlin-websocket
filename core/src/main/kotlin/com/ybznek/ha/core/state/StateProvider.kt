package com.ybznek.ha.core.state

import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.data.EntityState

interface StateProvider<T : TypedEntity> {
    val state: EntityState<T>
}