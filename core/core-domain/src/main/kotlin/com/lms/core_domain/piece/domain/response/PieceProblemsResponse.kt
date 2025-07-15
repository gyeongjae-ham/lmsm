package com.lms.core_domain.piece.domain.response

data class PieceProblemsResponse(
    val pieceId: Long,
    val pieceName: String,
    val teacherId: Long,
    val problemCount: Int,
    val problems: List<ProblemResponse>
)

data class ProblemResponse(
    val problemId: Long,
    val unitCode: String,
    val level: Int,
    val problemType: String,
    val sequence: Int
)