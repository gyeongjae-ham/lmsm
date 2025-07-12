package com.lms.core.domain.problem.domain.problem

import com.lms.core.domain.problem.domain.problem.exception.InvalidValueException
import com.lms.core.enum.Level
import kotlin.math.roundToInt

data class RatioCalculateResult(
    val lowCount: Int = 0,
    val mediumCount: Int = 0,
    val highCount: Int = 0
)

class ProblemRatioCalculator {
    companion object {
        private val LOW_RATIO = Triple(0.5, 0.3, 0.2)
        private val MEDIUM_RATIO = Triple(0.25, 0.5, 0.25)
        private val HIGH_RATIO = Triple(0.2, 0.3, 0.5)

        private const val LOW_DEDUCTION_INDEX = 2
        private const val MEDIUM_DEDUCTION_INDEX = 0
        private const val HIGH_DEDUCTION_INDEX = 0
    }

    fun calculate(level: Level, totalProblemCount: Int): RatioCalculateResult {
        if (totalProblemCount <= 0) {
            throw InvalidValueException()
        }

        val ratios = when (level) {
            Level.LOW -> LOW_RATIO
            Level.MEDIUM -> MEDIUM_RATIO
            Level.HIGH -> HIGH_RATIO
        }

        val counts = listOf(
            (totalProblemCount * ratios.first).roundToInt(),
            (totalProblemCount * ratios.second).roundToInt(),
            (totalProblemCount * ratios.third).roundToInt()
        ).toMutableList()

        val minRatioIndex = when (level) {
            Level.LOW -> LOW_DEDUCTION_INDEX
            Level.MEDIUM -> MEDIUM_DEDUCTION_INDEX
            Level.HIGH -> HIGH_DEDUCTION_INDEX
        }

        val excess = counts.sum() - totalProblemCount
        if (excess > 0) {
            counts[minRatioIndex] = maxOf(0, counts[minRatioIndex] - excess)
        }

        return RatioCalculateResult(
            lowCount = counts[0],
            mediumCount = counts[1],
            highCount = counts[2]
        )
    }
}
