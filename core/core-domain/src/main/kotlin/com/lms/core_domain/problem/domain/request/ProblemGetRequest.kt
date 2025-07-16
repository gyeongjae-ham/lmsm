package com.lms.core_domain.problem.domain.request

import com.lms.core_common.enum.Level
import com.lms.core_common.enum.ProblemType

data class ProblemGetRequest(
    val totalCount: Int,
    val unitCodeList: List<String>,
    val level: Level,
    val problemType: ProblemType
)
