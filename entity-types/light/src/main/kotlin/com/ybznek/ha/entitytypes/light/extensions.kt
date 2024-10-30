package com.ybznek.ha.entitytypes.light

import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.TypedEntity
import com.ybznek.ha.core.data.EntityState
import com.ybznek.ha.typed.EntityId



val EntityState<Light>.isOn get() = (state == "ON")

suspend fun EntityId<Light>.turnOn(haClient: HaClient) =
    haClient.callService(
        domain = "light",
        service = "turn_on",
        serviceData = mapOf(
            "target" to mapOf(
                "entity_id" to entityId
            )
        )
    )