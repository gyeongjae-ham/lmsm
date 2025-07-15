package com.lms.core_domain.studentanswer.domain

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.user.domain.User

class StudentAnswer(
    private val studentAnswerId: StudentAnswerId? = null,
    private val studentId: User.UserId,
    private val pieceId: Piece.PieceId,
    private val problemId: Problem.ProblemId,
    private val studentAnswer: String,
    private val isCorrect: Boolean,
) {
    val id: StudentAnswerId
        get() = requireNotNull(studentAnswerId) { "StudentAnswer ID is null" }

    fun getStudentId(): User.UserId = studentId
    fun getPieceId(): Piece.PieceId = pieceId
    fun getProblemId(): Problem.ProblemId = problemId
    fun getStudentAnswer(): String = studentAnswer
    fun isCorrect(): Boolean = isCorrect

    companion object {
        fun score(
            studentId: User.UserId,
            pieceId: Piece.PieceId,
            problemId: Problem.ProblemId,
            studentAnswer: String,
            correctAnswer: String
        ): StudentAnswer {
            val isCorrect = evaluateAnswer(studentAnswer, correctAnswer)
            return StudentAnswer(
                studentId = studentId,
                pieceId = pieceId,
                problemId = problemId,
                studentAnswer = studentAnswer,
                isCorrect = isCorrect,
            )
        }

        private fun evaluateAnswer(studentAnswer: String, correctAnswer: String): Boolean {
            val normalizedStudentAnswer = studentAnswer.trim().replace("\\s+".toRegex(), "")
            val normalizedCorrectAnswer = correctAnswer.trim().replace("\\s+".toRegex(), "")
            return normalizedStudentAnswer.equals(normalizedCorrectAnswer, ignoreCase = true)
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StudentAnswer) return false

        if (isCorrect != other.isCorrect) return false
        if (studentAnswerId != other.studentAnswerId) return false
        if (studentId != other.studentId) return false
        if (pieceId != other.pieceId) return false
        if (problemId != other.problemId) return false
        if (studentAnswer != other.studentAnswer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isCorrect.hashCode()
        result = 31 * result + (studentAnswerId?.hashCode() ?: 0)
        result = 31 * result + studentId.hashCode()
        result = 31 * result + pieceId.hashCode()
        result = 31 * result + problemId.hashCode()
        result = 31 * result + studentAnswer.hashCode()
        return result
    }

    data class StudentAnswerId(val value: Long)
}
