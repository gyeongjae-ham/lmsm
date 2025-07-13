package com.lms.core_domain.problem.domain

import com.lms.core_domain.enum.ProblemType
import com.lms.core_domain.exception.BusinessException
import com.lms.core_domain.problem.domain.response.ProblemFilterResponse

data class Problem(
    private val problemId: ProblemId? = null,
    val unitCode: String,
    val level: Int,
    val problemType: ProblemType,
    val answer: String
) {
    val id: ProblemId
        get() = requireNotNull(problemId) { BusinessException("Problem id is null!") }

    data class ProblemId(val value: Long)
}

fun Problem.toProblemFilterResponse() = ProblemFilterResponse(
    id = this.id.value,
    answer = this.answer,
    unitCode = this.unitCode,
    level = this.level,
    problemType = this.problemType,
)
