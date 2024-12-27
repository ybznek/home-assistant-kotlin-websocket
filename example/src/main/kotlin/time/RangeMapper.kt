package time

import kotlin.math.max
import kotlin.math.min

class RangeMapper(
    val inLow: Double,
    val inHigh: Double,
    val outLow: Double,
    val ratio: Double,
    val withoutReverse: Boolean
) {

    fun mapVal(inputValue: Double): Double {
        val sanitized = inputValue.coerceIn(inLow, inHigh)

        return when (withoutReverse) {
            true -> mapToOutputRange(sanitized - inLow)
            false -> mapToOutputRange(inHigh - inLow - (sanitized - inLow))
        }
    }

    private fun mapToOutputRange(inputValue: Double) =
        (inputValue * ratio) + outLow

    companion object {

        fun forRanges(inFrom: Double, inTo: Double, outFrom: Double, outTo: Double): RangeMapper {
            val inLow = min(inFrom, inTo)
            val inHigh = max(inFrom, inTo)
            val inDiff = inHigh - inLow

            val outLow = min(outFrom, outTo)
            val outHigh = max(outFrom, outTo)
            val outDiff = outHigh - outLow

            val ratio = outDiff / inDiff

            val withoutReverse = (inLow == inFrom) == (outLow == outFrom)
            return RangeMapper(inLow, inHigh, outLow, ratio, withoutReverse)
        }
    }
}