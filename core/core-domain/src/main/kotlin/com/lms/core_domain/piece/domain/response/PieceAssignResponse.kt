package com.lms.core_domain.piece.domain.response

data class PieceAssignResponse(
    val pieceId: Long,
    val pieceName: String,
    val assignedStudentCount: Int,
    val skippedStudentCount: Int,
)
