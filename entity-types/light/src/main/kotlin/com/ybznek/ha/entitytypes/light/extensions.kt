package com.ybznek.ha.entitytypes.light

import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.HassServiceTarget
import com.ybznek.ha.core.data.EntityState

val EntityState<Light>.isOn get() = (state == "ON")


suspend fun EntityId<Light>.turnOn(haClient: HaClient, target: HassServiceTarget) =
    haClient.callService(
        domain = "light",
        service = "turn_on",
        serviceData = mapOf(
            "target" to target
        )
    )