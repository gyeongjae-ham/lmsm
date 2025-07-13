package com.lms.core_domain.problem.domain

import com.lms.core_common.enum.Level

class ProblemSelector(
    private val ratioResult: RatioCalculateResult,
    private val problems: List<Problem>
) {
    fun extract(): List<Problem> {
        val problemsByLevel = problems.groupBy { Level.fromInt(it.level) }
        return listOf(
            (problemsByLevel[Level.LOW] ?: emptyList()).shuffled().take(ratioResult.lowCount),
            (problemsByLevel[Level.MEDIUM] ?: emptyList()).shuffled().take(ratioResult.mediumCount),
            (problemsByLevel[Level.HIGH] ?: emptyList()).shuffled().take(ratioResult.highCount)
        ).flatten().shuffled()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProblemSelector) return false
        return ratioResult == other.ratioResult && problems == other.problems
    }

    override fun hashCode(): Int {
        return 31 * ratioResult.hashCode() + problems.hashCode()
    }
}
