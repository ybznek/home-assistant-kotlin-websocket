package com.ybznek.ha.typed

import com.ybznek.ha.core.EntityIdString
import com.ybznek.ha.core.HaClient.StateProvider
import com.ybznek.ha.core.TypedEntity

@Suppress("unused")
@JvmInline
value class EntityId<T : TypedEntity>(val entityId: EntityIdString)
@Suppress("UNCHECKED_CAST")
operator fun <T : TypedEntity> Map<EntityIdString, StateProvider<TypedEntity>>.get(entity: EntityId<T>) =
    get(entity.entityId) as StateProvider<T>?
