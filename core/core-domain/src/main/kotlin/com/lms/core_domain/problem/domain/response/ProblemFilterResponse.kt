package com.lms.core_domain.problem.domain.response

import com.lms.core_domain.enum.ProblemType

data class ProblemFilterResponse(
    val id: Long,
    val answer: String,
    val unitCode: String,
    val level: Int,
    val problemType: ProblemType,
)
