package com.lms.core_domain.studentanswer.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.studentanswer.domain.repository.StudentAnswerRepository
import com.lms.core_domain.user.domain.User
import org.springframework.stereotype.Service

@Service
class StudentAnswerFinder(
    private val studentAnswerRepository: StudentAnswerRepository
) {
    
    fun findByStudentIdAndPieceId(studentId: User.UserId, pieceId: Piece.PieceId): List<StudentAnswer> {
        return studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId)
    }
    
    fun hasSubmittedAnswers(studentId: User.UserId, pieceId: Piece.PieceId): Boolean {
        return studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId).isNotEmpty()
    }
}