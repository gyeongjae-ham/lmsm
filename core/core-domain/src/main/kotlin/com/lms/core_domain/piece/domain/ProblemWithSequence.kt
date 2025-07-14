package com.lms.core_domain.piece.domain

import com.lms.core_domain.problem.domain.Problem

data class ProblemWithSequence(
    val problem: Problem,
    val sequence: Int
) {
    companion object {
        fun fromProblems(problems: List<Problem>): List<ProblemWithSequence> {
            return problems.mapIndexed { index, problem ->
                ProblemWithSequence(problem, (index + 1) * 10)
            }
        }
    }
}
