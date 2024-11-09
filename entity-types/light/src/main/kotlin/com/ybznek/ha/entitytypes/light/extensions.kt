package com.ybznek.ha.entitytypes.light

import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.HassServiceTarget
import com.ybznek.ha.core.data.EntityState

val EntityState<Light>.isOn get() = (state == "ON")

data class RgbColor(
    val r: UByte = 0.toUByte(),
    val g: UByte = 0.toUByte(),
    val b: UByte = 0.toUByte()
) {
    constructor(
        r: Int = 0,
        g: Int = 0,
        b: Int = 0
    ) : this(r = r.toUByte(), g = g.toUByte(), b = b.toUByte())

    fun toList(): List<Int> = listOf(r.toInt(), g.toInt(), b.toInt())
}
typealias Brightness = UByte

suspend fun EntityId<Light>.turnOn(
    haClient: HaClient,
    rgbColor: RgbColor? = null,
    brightness: Brightness? = null
) = haClient.callService(
    domain = "light",
    service = "turn_on",
    data = mapOf(
        "target" to HassServiceTarget.entity(this@turnOn),
        "service_data" to buildMap {
            rgbColor?.run {
                put("rgb_color", listOf(r.toInt(), g.toInt(), b.toInt()))
            }
            brightness?.let {
                put("brightness", it.toInt())
            }
        })
)

suspend fun EntityId<Light>.turnOff(haClient: HaClient) =
    haClient.callService(
        domain = "light",
        service = "turn_off",
        data = mapOf(
            "target" to HassServiceTarget.entity(this)
        )
    )