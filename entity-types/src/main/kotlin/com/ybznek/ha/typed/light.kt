package com.ybznek.ha.typed

import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.EntityIdString
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.state.StateProvider


@Suppress("UNCHECKED_CAST")
operator fun <T : TypedEntity> Map<EntityIdString, StateProvider<TypedEntity>>.get(entity: EntityId<T>) =
    get(entity.entityId) as StateProvider<T>?
