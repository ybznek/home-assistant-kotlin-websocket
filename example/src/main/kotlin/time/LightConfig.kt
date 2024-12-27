package time

import time.LightConfig.RangeType.*


class LightConfig {
    enum class RangeType {
        DayToNight,
        NightToDay,
        Day,
        Night
    }

    companion object {

        fun build(dayColor: Int, nightColor: Int, list: List<LightRange>): List<LightMapper> =
            list.map { range ->
                val (from, to) = toDir(range, dayColor, nightColor)
                val mapper = RangeMapper.forRanges(range.minHour, range.maxHour, from.toDouble(), to.toDouble())
                LightMapper(range.minHour, range.maxHour, mapper)
            }

        private fun toDir(range: LightRange, dayColor: Int, nightColor: Int): Pair<Int, Int> =
            when (range.type) {
                DayToNight -> (dayColor to nightColor)
                NightToDay -> (nightColor to dayColor)
                Day -> (dayColor to dayColor)
                Night -> (nightColor to nightColor)
            }

        data class LightRange(val minHour: Double, val maxHour: Double, val type: RangeType)
        data class LightMapper(val minHour: Double, val maxHour: Double, val mapper: RangeMapper)
    }
}