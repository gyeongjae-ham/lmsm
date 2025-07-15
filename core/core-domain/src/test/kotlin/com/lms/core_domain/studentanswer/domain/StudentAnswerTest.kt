package com.lms.core_domain.studentanswer.domain

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StudentAnswerTest {

    @Test
    fun `정답일 때 채점 결과가 true를 반환한다`() {
        val studentAnswer = StudentAnswer.score(
            studentId = User.UserId(1L),
            pieceId = Piece.PieceId(1L),
            problemId = Problem.ProblemId(1L),
            studentAnswer = "정답",
            correctAnswer = "정답"
        )

        assertThat(studentAnswer.isCorrect()).isTrue()
        assertThat(studentAnswer.getStudentAnswer()).isEqualTo("정답")
    }

    @Test
    fun `오답일 때 채점 결과가 false를 반환한다`() {
        val studentAnswer = StudentAnswer.score(
            studentId = User.UserId(1L),
            pieceId = Piece.PieceId(1L),
            problemId = Problem.ProblemId(1L),
            studentAnswer = "오답",
            correctAnswer = "정답"
        )

        assertThat(studentAnswer.isCorrect()).isFalse()
        assertThat(studentAnswer.getStudentAnswer()).isEqualTo("오답")
    }

    @Test
    fun `대소문자를 구분하지 않고 채점한다`() {
        val studentAnswer = StudentAnswer.score(
            studentId = User.UserId(1L),
            pieceId = Piece.PieceId(1L),
            problemId = Problem.ProblemId(1L),
            studentAnswer = "Answer",
            correctAnswer = "answer"
        )

        assertThat(studentAnswer.isCorrect()).isTrue()
    }

    @Test
    fun `앞뒤 공백을 제거하고 채점한다`() {
        val studentAnswer = StudentAnswer.score(
            studentId = User.UserId(1L),
            pieceId = Piece.PieceId(1L),
            problemId = Problem.ProblemId(1L),
            studentAnswer = "  정답  ",
            correctAnswer = "정답"
        )

        assertThat(studentAnswer.isCorrect()).isTrue()
    }

    @Test
    fun `중간 공백을 제거하고 채점한다`() {
        val studentAnswer = StudentAnswer.score(
            studentId = User.UserId(1L),
            pieceId = Piece.PieceId(1L),
            problemId = Problem.ProblemId(1L),
            studentAnswer = "정 답 입 니 다",
            correctAnswer = "정답입니다"
        )

        assertThat(studentAnswer.isCorrect()).isTrue()
    }

    @Test
    fun `앞뒤 공백과 중간 공백을 모두 제거하고 채점한다`() {
        val studentAnswer = StudentAnswer.score(
            studentId = User.UserId(1L),
            pieceId = Piece.PieceId(1L),
            problemId = Problem.ProblemId(1L),
            studentAnswer = "  정 답 입 니 다  ",
            correctAnswer = "정답입니다"
        )

        assertThat(studentAnswer.isCorrect()).isTrue()
    }

    @Test
    fun `학생 답안 정보가 정확히 저장된다`() {
        val studentId = User.UserId(1L)
        val pieceId = Piece.PieceId(1L)
        val problemId = Problem.ProblemId(1L)
        val studentAnswerText = "학생 답안"

        val studentAnswer = StudentAnswer.score(
            studentId = studentId,
            pieceId = pieceId,
            problemId = problemId,
            studentAnswer = studentAnswerText,
            correctAnswer = "정답"
        )

        assertThat(studentAnswer.getStudentId()).isEqualTo(studentId)
        assertThat(studentAnswer.getPieceId()).isEqualTo(pieceId)
        assertThat(studentAnswer.getProblemId()).isEqualTo(problemId)
    }
}
