package com.lms.core.domain.problem.domain.problem

import com.lms.core.enum.ProblemType
import com.lms.core.exception.BusinessException

data class Problem(
    private val problemId: ProblemId? = null,
    val unitCode: String,
    val level: Int,
    val problemType: ProblemType,
    val answer: String
) {
    val id: ProblemId
        get() = requireNotNull(problemId) { BusinessException("Problem id is null!") }

    data class ProblemId(val id: Long)
}
