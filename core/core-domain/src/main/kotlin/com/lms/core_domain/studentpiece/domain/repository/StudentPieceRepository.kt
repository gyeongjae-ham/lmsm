package com.lms.core_domain.studentpiece.domain.repository

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentpiece.domain.StudentPiece
import com.lms.core_domain.user.domain.User

interface StudentPieceRepository {
    fun saveAll(studentPieces: List<StudentPiece>): List<StudentPiece>
    fun findByStudentIdsAndPieceId(studentIds: List<User.UserId>, pieceId: Piece.PieceId): List<StudentPiece>
}
