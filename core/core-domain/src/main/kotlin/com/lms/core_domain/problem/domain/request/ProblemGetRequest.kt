package com.lms.core_domain.problem.domain.request

import com.lms.core_domain.enum.Level
import com.lms.core_domain.enum.ProblemType

data class ProblemGetRequest(
    val totalCount: Int,
    val unitCodeList: List<String>,
    val level: Level,
    val problemType: ProblemType
)
