package com.lms.core_domain.studentanswer.domain.repository

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.user.domain.User

interface StudentAnswerRepository {
    fun saveAll(studentAnswers: List<StudentAnswer>): List<StudentAnswer>
    fun findByStudentIdAndPieceId(studentId: User.UserId, pieceId: Piece.PieceId): List<StudentAnswer>
    fun findByPieceId(pieceId: Piece.PieceId): List<StudentAnswer>
}