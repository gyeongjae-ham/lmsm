package com.lms.core_domain.studentanswer.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.studentanswer.domain.repository.StudentAnswerRepository
import com.lms.core_domain.user.domain.User
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StudentAnswerFinderTest {

    @MockK
    private lateinit var studentAnswerRepository: StudentAnswerRepository

    private lateinit var studentAnswerFinder: StudentAnswerFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        studentAnswerFinder = StudentAnswerFinder(studentAnswerRepository)
    }

    @Test
    fun `학생이 학습지에 답변을 제출했는지 확인한다 - 제출했음`() {
        val studentId = User.UserId(1L)
        val pieceId = Piece.PieceId(1L)
        val studentAnswers = listOf(
            createStudentAnswer(studentId, pieceId, Problem.ProblemId(1L))
        )

        every { studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId) } returns studentAnswers

        val result = studentAnswerFinder.hasSubmittedAnswers(studentId, pieceId)

        assertThat(result).isTrue()
        verify { studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId) }
    }

    @Test
    fun `학생이 학습지에 답변을 제출했는지 확인한다 - 제출 안함`() {
        val studentId = User.UserId(1L)
        val pieceId = Piece.PieceId(1L)

        every { studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId) } returns emptyList()

        val result = studentAnswerFinder.hasSubmittedAnswers(studentId, pieceId)

        assertThat(result).isFalse()
        verify { studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId) }
    }

    @Test
    fun `학생의 학습지 답변 목록을 조회한다`() {
        val studentId = User.UserId(1L)
        val pieceId = Piece.PieceId(1L)
        val studentAnswers = listOf(
            createStudentAnswer(studentId, pieceId, Problem.ProblemId(1L)),
            createStudentAnswer(studentId, pieceId, Problem.ProblemId(2L))
        )

        every { studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId) } returns studentAnswers

        val result = studentAnswerFinder.findByStudentIdAndPieceId(studentId, pieceId)

        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(studentAnswers)
        verify { studentAnswerRepository.findByStudentIdAndPieceId(studentId, pieceId) }
    }

    private fun createStudentAnswer(
        studentId: User.UserId,
        pieceId: Piece.PieceId,
        problemId: Problem.ProblemId
    ): StudentAnswer {
        return StudentAnswer(
            studentAnswerId = StudentAnswer.StudentAnswerId(1L),
            studentId = studentId,
            pieceId = pieceId,
            problemId = problemId,
            studentAnswer = "답안",
            isCorrect = true
        )
    }
}
