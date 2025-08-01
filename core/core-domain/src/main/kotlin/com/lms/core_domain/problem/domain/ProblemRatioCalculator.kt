package com.lms.core_domain.problem.domain

import com.lms.core_common.enum.Level
import com.lms.core_domain.problem.domain.exception.InvalidValueException
import org.springframework.stereotype.Component
import kotlin.math.roundToInt

data class RatioCalculateResult(
    val lowCount: Int = 0,
    val mediumCount: Int = 0,
    val highCount: Int = 0
)

@Component
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
