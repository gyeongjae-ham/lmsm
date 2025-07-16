package com.lms.db.core.studentanswer

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.user.domain.User
import com.lms.db.core.config.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "student_answers")
class StudentAnswerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "student_id", nullable = false)
    val studentId: Long,

    @Column(name = "piece_id", nullable = false)
    val pieceId: Long,

    @Column(name = "problem_id", nullable = false)
    val problemId: Long,

    @Column(name = "student_answer", nullable = false, length = 1000)
    val studentAnswer: String,

    @Column(name = "is_correct", nullable = false)
    val isCorrect: Boolean,
) : BaseEntity()

fun StudentAnswer.toEntity(): StudentAnswerEntity {
    return StudentAnswerEntity(
        studentId = this.getStudentId().value,
        pieceId = this.getPieceId().value,
        problemId = this.getProblemId().value,
        studentAnswer = this.getStudentAnswer(),
        isCorrect = this.isCorrect(),
    )
}

fun StudentAnswerEntity.toDomain(): StudentAnswer {
    return StudentAnswer(
        studentAnswerId = StudentAnswer.StudentAnswerId(this.id!!),
        studentId = User.UserId(this.studentId),
        pieceId = Piece.PieceId(this.pieceId),
        problemId = com.lms.core_domain.problem.domain.Problem.ProblemId(this.problemId),
        studentAnswer = this.studentAnswer,
        isCorrect = this.isCorrect,
    )
}
