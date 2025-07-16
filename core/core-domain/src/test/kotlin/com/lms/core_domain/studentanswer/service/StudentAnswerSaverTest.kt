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

class StudentAnswerSaverTest {

    @MockK
    private lateinit var studentAnswerRepository: StudentAnswerRepository

    private lateinit var studentAnswerSaver: StudentAnswerSaver

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        studentAnswerSaver = StudentAnswerSaver(studentAnswerRepository)
    }

    @Test
    fun `학생 답안 목록을 저장한다`() {
        val studentAnswers = listOf(
            createStudentAnswer(1L, User.UserId(1L), Piece.PieceId(1L), Problem.ProblemId(1L)),
            createStudentAnswer(2L, User.UserId(1L), Piece.PieceId(1L), Problem.ProblemId(2L))
        )
        val savedAnswers: List<StudentAnswer> = studentAnswers

        every { studentAnswerRepository.saveAll(studentAnswers) } returns savedAnswers

        val result = studentAnswerSaver.saveAll(studentAnswers)

        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(savedAnswers)
        verify { studentAnswerRepository.saveAll(studentAnswers) }
    }

    private fun createStudentAnswer(
        id: Long,
        studentId: User.UserId,
        pieceId: Piece.PieceId,
        problemId: Problem.ProblemId
    ): StudentAnswer {
        return StudentAnswer(
            studentAnswerId = StudentAnswer.StudentAnswerId(id),
            studentId = studentId,
            pieceId = pieceId,
            problemId = problemId,
            studentAnswer = "답안",
            isCorrect = true
        )
    }
}