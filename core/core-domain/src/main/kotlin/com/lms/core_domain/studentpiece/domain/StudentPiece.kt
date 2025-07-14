package com.lms.core_domain.studentpiece.domain

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.user.domain.User

class StudentPiece(
    private val studentPieceId: StudentPieceId? = null,
    private val studentId: User.UserId,
    private val pieceId: Piece.PieceId
) {
    val id: StudentPieceId
        get() = requireNotNull(studentPieceId) { "StudentPiece ID is null" }

    fun getStudentId(): User.UserId = studentId
    fun getPieceId(): Piece.PieceId = pieceId

    data class StudentPieceId(val value: Long)

    companion object {
        fun assign(studentId: User.UserId, pieceId: Piece.PieceId): StudentPiece {
            return StudentPiece(
                studentPieceId = null,
                studentId = studentId,
                pieceId = pieceId
            )
        }
    }
}
