package com.lms.core.domain.problem.domain.problem.request

import com.lms.core.enum.Level
import com.lms.core.enum.ProblemType

data class ProblemGetRequest(
    val totalCount: Int,
    val unitCodeList: List<String>,
    val level: Level,
    val problemType: ProblemType
)
