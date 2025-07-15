package com.lms.db.core.studentanswer

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.studentanswer.domain.repository.StudentAnswerRepository
import com.lms.core_domain.user.domain.User
import org.springframework.stereotype.Repository

@Repository
class StudentAnswerRepositoryImpl(
    private val studentAnswerJpaRepository: StudentAnswerJpaRepository
) : StudentAnswerRepository {

    override fun saveAll(studentAnswers: List<StudentAnswer>): List<StudentAnswer> {
        val entities = studentAnswers.map { it.toEntity() }
        val savedEntities = studentAnswerJpaRepository.saveAll(entities)
        return savedEntities.map { it.toDomain() }
    }

    override fun findByStudentIdAndPieceId(studentId: User.UserId, pieceId: Piece.PieceId): List<StudentAnswer> {
        val entities = studentAnswerJpaRepository.findByStudentIdAndPieceId(studentId.value, pieceId.value)
        return entities.map { it.toDomain() }
    }
}
