package com.lms.core_domain.piece.domain.response

data class PieceAnalyzeResponse(
    val pieceId: Long,
    val pieceName: String,
    val teacherId: Long,
    val totalAssignedStudents: Int,
    val submittedStudents: Int,
    val submissionRate: Double,
    val studentStatistics: List<StudentStatistic>,
    val problemStatistics: List<ProblemStatistic>
)

data class StudentStatistic(
    val studentId: Long,
    val totalProblems: Int,
    val correctCount: Int,
    val scoreRate: Double,
    val hasSubmitted: Boolean
)

data class ProblemStatistic(
    val problemId: Long,
    val unitCode: String,
    val level: Int,
    val problemType: String,
    val totalSubmissions: Int,
    val correctSubmissions: Int,
    val correctRate: Double
)

data class PieceOverallStatistic(
    val totalAssignedStudents: Int,
    val submittedStudents: Int,
    val submissionRate: Double
)