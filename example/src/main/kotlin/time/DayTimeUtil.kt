package time

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import time.LightConfig.Companion.LightMapper
import time.LightConfig.Companion.LightRange
import time.LightConfig.RangeType.*
import kotlin.math.roundToInt

class DayTimeUtil(
    private val clock: Clock = Clock.System,
    private val zone: TimeZone = TimeZone.currentSystemDefault()
) {
    companion object {
        const val DAY_COLOR = 250
        const val NIGHT_COLOR = 454
    }

    private val fromHour: List<LightMapper> =
        LightConfig.build(
            DAY_COLOR, NIGHT_COLOR, listOf(
                LightRange(21.0, 24.0, Night),
                LightRange(0.0, 4.0, Night),
                LightRange(4.0, 5.0, NightToDay),
                LightRange(5.0, 18.0, Day),
                LightRange(18.0, 21.0, DayToNight)
            )
        )

    val currentHour: Double
        get() {
            var now = clock.now().toLocalDateTime(zone)
            return now.hour + now.minute / 60.0
        }

    val currentColor: Int = getColorForHour(this.currentHour)

    internal fun getColorForHour(currentHour: Double) =
        getMapper(currentHour).mapper.mapVal(currentHour).roundToInt()

    private fun getMapper(hour: Double): LightMapper =
        fromHour.first { mapper -> hour >= mapper.minHour && hour <= mapper.maxHour }

    fun nowMs(): Long =
        clock.now().toEpochMilliseconds()
}