package com.ybznek.ha.core

/**
 * Base class for entity types
 * Used for extension methods
 */
interface TypedEntity

@Suppress("unused")
@JvmInline
value class EntityId<T : TypedEntity>(
    val entityId: EntityIdString
)