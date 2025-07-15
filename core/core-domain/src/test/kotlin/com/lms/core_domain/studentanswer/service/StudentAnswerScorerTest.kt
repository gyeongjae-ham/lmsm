package com.lms.core_domain.studentanswer.service

import com.lms.core_common.enum.ProblemType
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.ProblemWithSequence
import com.lms.core_domain.piece.domain.request.PieceScoreRequest
import com.lms.core_domain.piece.domain.request.StudentAnswerRequest
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StudentAnswerScorerTest {

    private val studentAnswerScorer = StudentAnswerScorer()

    @Test
    fun `여러 문제를 동시에 채점하고 결과를 반환한다`() {
        val pieceId = Piece.PieceId(1L)
        val studentId = User.UserId(2L)
        val piece = createPieceWithProblems(pieceId)
        val request = PieceScoreRequest(
            studentId = studentId.value,
            answers = listOf(
                StudentAnswerRequest(problemId = 1L, studentAnswer = "정답1"),
                StudentAnswerRequest(problemId = 2L, studentAnswer = "오답2")
            )
        )

        val result = studentAnswerScorer.scoreAnswers(studentId, pieceId, piece, request)

        assertThat(result).hasSize(2)

        val result1 = result.find { it.first.problemId == 1L }!!
        assertThat(result1.first.studentAnswer).isEqualTo("정답1")
        assertThat(result1.first.correctAnswer).isEqualTo("정답1")
        assertThat(result1.first.isCorrect).isTrue()
        assertThat(result1.second.isCorrect()).isTrue()

        val result2 = result.find { it.first.problemId == 2L }!!
        assertThat(result2.first.studentAnswer).isEqualTo("오답2")
        assertThat(result2.first.correctAnswer).isEqualTo("정답2")
        assertThat(result2.first.isCorrect).isFalse()
        assertThat(result2.second.isCorrect()).isFalse()
    }

    @Test
    fun `모든 문제를 맞췄을 때 정답 처리된다`() {
        val pieceId = Piece.PieceId(1L)
        val studentId = User.UserId(2L)
        val piece = createPieceWithProblems(pieceId)
        val request = PieceScoreRequest(
            studentId = studentId.value,
            answers = listOf(
                StudentAnswerRequest(problemId = 1L, studentAnswer = "정답1"),
                StudentAnswerRequest(problemId = 2L, studentAnswer = "정답2")
            )
        )

        val result = studentAnswerScorer.scoreAnswers(studentId, pieceId, piece, request)

        assertThat(result).hasSize(2)
        assertThat(result.all { it.first.isCorrect }).isTrue()
        assertThat(result.all { it.second.isCorrect() }).isTrue()
    }

    private fun createPieceWithProblems(pieceId: Piece.PieceId): Piece {
        val problems = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "정답1"
                ),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "정답2"
                ),
                sequence = 20
            )
        )
        return Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = problems
        )
    }
}
