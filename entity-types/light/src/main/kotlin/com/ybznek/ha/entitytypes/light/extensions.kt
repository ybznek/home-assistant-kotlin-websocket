package com.ybznek.ha.entitytypes.light

import com.ybznek.ha.core.EntityId
import com.ybznek.ha.core.HaClient
import com.ybznek.ha.core.HassServiceTarget
import com.ybznek.ha.core.data.EntityState

val EntityState<out Light>.isOn get() = (state == "ON")

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

    companion object {
        val BLACK = RgbColor(0, 0, 0)
        val WHITE = RgbColor(255, 255, 255)
        val RED = RgbColor(255, 0, 0)
        val GREEN = RgbColor(0, 255, 0)
        val BLUE = RgbColor(0, 0, 255)
        val PURPLE = RgbColor(255, 0, 255)
    }
}

@JvmInline
value class Brightness(val value: UByte) {
    
    constructor(value: Int) : this(value.toUByte())

    companion object {
        val MAX = Brightness(255)
    }
}

suspend fun EntityId<out Light>.turnOn(
    haClient: HaClient,
    rgbColor: RgbColor? = null,
    transition: Int? = null,
    colorTemp: Int? = null,
    brightness: Brightness? = Brightness.MAX
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
                put("brightness", it.value.toInt())
            }
            colorTemp?.let {
                put("color_temp", it)
            }
            transition?.let {
                put("transition", it)
            }
        })
)

suspend fun EntityId<out Light>.turnOff(haClient: HaClient) =
    haClient.callService(
        domain = "light",
        service = "turn_off",
        data = mapOf(
            "target" to HassServiceTarget.entity(this)
        )
    )