package com.ybznek.ha.core

data class HassServiceTarget(
    val entityId: List<String>? = null,
    val deviceId: List<String>? = null,
    val areaId: List<String>? = null,
    val floorId: List<String>? = null,
    val labelId: List<String>? = null
) {
    companion object {
        fun entity(id: EntityIdString) = HassServiceTarget(entityId = listOf(id))
        fun entity(id: EntityId<*>) = entity(id.entityId)
    }
}