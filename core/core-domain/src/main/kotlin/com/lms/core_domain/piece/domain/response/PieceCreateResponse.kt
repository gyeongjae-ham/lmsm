package com.lms.core_domain.piece.domain.response

data class PieceCreateResponse(
    val id: Long,
    val name: String,
    val teacherId: Long,
    val problemCount: Int
)
