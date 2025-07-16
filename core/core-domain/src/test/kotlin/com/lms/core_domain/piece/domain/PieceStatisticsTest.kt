package com.lms.core_domain.piece.domain

import com.lms.core_common.enum.ProblemType
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test

class PieceStatisticsTest {

    @Test
    fun `학생 통계를 올바르게 계산한다`() {
        val piece = createPieceWithProblems()
        val assignedStudents = listOf(User.UserId(1L), User.UserId(2L), User.UserId(3L))
        val studentAnswers = listOf(
            // 학생 1: 2문제 중 2문제 정답 (100%)
            createStudentAnswer(User.UserId(1L), Problem.ProblemId(1L), true),
            createStudentAnswer(User.UserId(1L), Problem.ProblemId(2L), true),
            // 학생 2: 2문제 중 1문제 정답 (50%)
            createStudentAnswer(User.UserId(2L), Problem.ProblemId(1L), true),
            createStudentAnswer(User.UserId(2L), Problem.ProblemId(2L), false)
            // 학생 3: 답변 미제출
        )

        val statistics = PieceStatistics(piece, assignedStudents, studentAnswers)
        val result = statistics.calculateStudentStatistics()

        assertThat(result).hasSize(3)
        
        // 학생 1 (100% 정답)
        val student1 = result.find { it.studentId == 1L }!!
        assertThat(student1.totalProblems).isEqualTo(2)
        assertThat(student1.correctCount).isEqualTo(2)
        assertThat(student1.scoreRate).isEqualTo(100.0)
        assertThat(student1.hasSubmitted).isTrue()
        
        // 학생 2 (50% 정답)
        val student2 = result.find { it.studentId == 2L }!!
        assertThat(student2.totalProblems).isEqualTo(2)
        assertThat(student2.correctCount).isEqualTo(1)
        assertThat(student2.scoreRate).isEqualTo(50.0)
        assertThat(student2.hasSubmitted).isTrue()
        
        // 학생 3 (미제출)
        val student3 = result.find { it.studentId == 3L }!!
        assertThat(student3.totalProblems).isEqualTo(0)
        assertThat(student3.correctCount).isEqualTo(0)
        assertThat(student3.scoreRate).isEqualTo(0.0)
        assertThat(student3.hasSubmitted).isFalse()
    }

    @Test
    fun `문제별 통계를 올바르게 계산한다`() {
        val piece = createPieceWithProblems()
        val assignedStudents = listOf(User.UserId(1L), User.UserId(2L), User.UserId(3L))
        val studentAnswers = listOf(
            // 문제 1: 2명 제출, 2명 정답 (100%)
            createStudentAnswer(User.UserId(1L), Problem.ProblemId(1L), true),
            createStudentAnswer(User.UserId(2L), Problem.ProblemId(1L), true),
            // 문제 2: 2명 제출, 1명 정답 (50%)
            createStudentAnswer(User.UserId(1L), Problem.ProblemId(2L), true),
            createStudentAnswer(User.UserId(2L), Problem.ProblemId(2L), false)
        )

        val statistics = PieceStatistics(piece, assignedStudents, studentAnswers)
        val result = statistics.calculateProblemStatistics()

        assertThat(result).hasSize(2)
        
        // 문제 1 (100% 정답률)
        val problem1 = result.find { it.problemId == 1L }!!
        assertThat(problem1.unitCode).isEqualTo("uc1580")
        assertThat(problem1.level).isEqualTo(1)
        assertThat(problem1.problemType).isEqualTo("SELECTION")
        assertThat(problem1.totalSubmissions).isEqualTo(2)
        assertThat(problem1.correctSubmissions).isEqualTo(2)
        assertThat(problem1.correctRate).isEqualTo(100.0)
        
        // 문제 2 (50% 정답률)
        val problem2 = result.find { it.problemId == 2L }!!
        assertThat(problem2.unitCode).isEqualTo("uc1580")
        assertThat(problem2.level).isEqualTo(2)
        assertThat(problem2.problemType).isEqualTo("SUBJECTIVE")
        assertThat(problem2.totalSubmissions).isEqualTo(2)
        assertThat(problem2.correctSubmissions).isEqualTo(1)
        assertThat(problem2.correctRate).isEqualTo(50.0)
    }

    @Test
    fun `전체 통계를 올바르게 계산한다`() {
        val piece = createPieceWithProblems()
        val assignedStudents = listOf(User.UserId(1L), User.UserId(2L), User.UserId(3L))
        val studentAnswers = listOf(
            // 학생 1, 2만 제출 (3명 중 2명 제출 = 66.67%)
            createStudentAnswer(User.UserId(1L), Problem.ProblemId(1L), true),
            createStudentAnswer(User.UserId(2L), Problem.ProblemId(1L), true)
        )

        val statistics = PieceStatistics(piece, assignedStudents, studentAnswers)
        val result = statistics.calculateOverallStatistics()

        assertThat(result.totalAssignedStudents).isEqualTo(3)
        assertThat(result.submittedStudents).isEqualTo(2)
        assertThat(result.submissionRate).isCloseTo(66.67, within(0.01))
    }

    @Test
    fun `출제받은 학생이 없을 때 통계를 올바르게 계산한다`() {
        val piece = createPieceWithProblems()
        val assignedStudents = emptyList<User.UserId>()
        val studentAnswers = emptyList<StudentAnswer>()

        val statistics = PieceStatistics(piece, assignedStudents, studentAnswers)
        val overallStats = statistics.calculateOverallStatistics()
        val studentStats = statistics.calculateStudentStatistics()
        val problemStats = statistics.calculateProblemStatistics()

        assertThat(overallStats.totalAssignedStudents).isEqualTo(0)
        assertThat(overallStats.submittedStudents).isEqualTo(0)
        assertThat(overallStats.submissionRate).isEqualTo(0.0)
        assertThat(studentStats).isEmpty()
        assertThat(problemStats).hasSize(2)
        assertThat(problemStats.all { it.totalSubmissions == 0 && it.correctRate == 0.0 }).isTrue()
    }

    private fun createPieceWithProblems(): Piece {
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
            pieceId = Piece.PieceId(1L),
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = problems
        )
    }

    private fun createStudentAnswer(
        studentId: User.UserId,
        problemId: Problem.ProblemId,
        isCorrect: Boolean
    ): StudentAnswer {
        return StudentAnswer(
            studentAnswerId = StudentAnswer.StudentAnswerId(1L),
            studentId = studentId,
            pieceId = Piece.PieceId(1L),
            problemId = problemId,
            studentAnswer = if (isCorrect) "정답" else "오답",
            isCorrect = isCorrect
        )
    }
}