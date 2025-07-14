package com.lms.core_domain.piece.domain

import com.lms.core_domain.user.domain.User

data class PieceAssignmentResult(
    val pieceId: Piece.PieceId,
    val newAssignments: List<User.UserId>,
    val skippedStudents: List<User.UserId>
) {
    fun hasNewAssignments(): Boolean = newAssignments.isNotEmpty()
    fun getAssignmentCount(): Int = newAssignments.size
    fun getSkippedCount(): Int = skippedStudents.size
}