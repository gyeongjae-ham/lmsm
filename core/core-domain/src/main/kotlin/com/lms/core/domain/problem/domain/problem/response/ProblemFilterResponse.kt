package com.lms.core.domain.problem.domain.problem.response

import com.lms.core.enum.ProblemType

data class ProblemFilterResponse(
    val id: Long,
    val answer: String,
    val unitCode: String,
    val level: Int,
    val problemType: ProblemType,
)
