package com.lms.core_domain.piece.domain.response

import com.lms.core_common.enum.ProblemType

data class ProblemOrderResponse(
    val pieceId: Long,
    val name: String,
    val problemCount: Int,
    val problems: List<ProblemWithSequenceResponse>,
)

data class ProblemWithSequenceResponse(
    val problemId: Long,
    val orderIndex: Int,
    val unitCode: String,
    val level: Int,
    val problemType: ProblemType,
    val answer: String,
)
