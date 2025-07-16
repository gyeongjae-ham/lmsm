package com.lms.core_domain.piece.domain.response

data class PieceScoreResponse(
    val pieceId: Long,
    val studentId: Long,
    val totalProblems: Int,
    val correctCount: Int,
    val scoreRate: Double,
    val results: List<ScoreResultResponse>
)

data class ScoreResultResponse(
    val problemId: Long,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
)