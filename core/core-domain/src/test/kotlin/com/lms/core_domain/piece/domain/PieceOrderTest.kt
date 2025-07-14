package com.lms.core_domain.piece.domain

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.problem.domain.Problem
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PieceOrderTest {

    private fun createTestPiece(): Piece {
        val problems = listOf(
            createProblem(1L),
            createProblem(2L),
            createProblem(3L),
            createProblem(4L)
        )
        val problemsWithSequence = problems.mapIndexed { index, problem ->
            ProblemWithSequence(problem, (index + 1) * 10) // 10, 20, 30, 40
        }

        return Piece(
            pieceId = Piece.PieceId(1L),
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = problemsWithSequence
        )
    }

    private fun createProblem(id: Long): Problem {
        return Problem(
            problemId = Problem.ProblemId(id),
            unitCode = "uc1580",
            level = 1,
            problemType = ProblemType.SELECTION,
            answer = "1"
        )
    }

    @Test
    fun `맨 앞으로 이동 - 중간값 사용`() {
        val piece = createTestPiece()
        // Problem 2(seq:20)를 맨 앞(position 0)으로 이동

        val reorderedPiece = piece.reorderProblem(problemId = 2L, targetPosition = 0)
        val sequences = reorderedPiece.getProblemsWithSequence().map { it.sequence }

        // 0과 10 사이의 중간값인 5가 사용되어야 함
        assertThat(sequences).containsExactly(5, 10, 30, 40)

        val problemIds = reorderedPiece.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(2L, 1L, 3L, 4L)
    }

    @Test
    fun `맨 뒤로 이동`() {
        val piece = createTestPiece()
        // Problem 2(seq:20)를 맨 뒤(position 3)로 이동

        val reorderedPiece = piece.reorderProblem(problemId = 2L, targetPosition = 3)
        val sequences = reorderedPiece.getProblemsWithSequence().map { it.sequence }

        // 마지막 sequence(40) + 10 = 50이 사용되어야 함
        assertThat(sequences).containsExactly(10, 30, 40, 50)

        val problemIds = reorderedPiece.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(1L, 3L, 4L, 2L)
    }

    @Test
    fun `중간으로 이동 - 중간값 사용`() {
        val piece = createTestPiece()
        // Problem 1(seq:10)을 position 2로 이동 (30과 40 사이)

        val reorderedPiece = piece.reorderProblem(problemId = 1L, targetPosition = 2)
        val sequences = reorderedPiece.getProblemsWithSequence().map { it.sequence }

        // 30과 40 사이의 중간값인 35가 사용되어야 함
        assertThat(sequences).containsExactly(20, 30, 35, 40)

        val problemIds = reorderedPiece.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(2L, 3L, 1L, 4L)
    }

    @Test
    fun `공간 부족시 전체 재정렬`() {
        // 정말 공간이 부족한 상황: 연속된 정수 sequence
        val problems = listOf(
            createProblem(1L),
            createProblem(2L),
            createProblem(3L)
        )
        val problemsWithSequence = listOf(
            ProblemWithSequence(problems[0], 1), // 1
            ProblemWithSequence(problems[1], 2), // 2  
            ProblemWithSequence(problems[2], 3)  // 3
        )

        val piece = Piece(
            pieceId = Piece.PieceId(1L),
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = problemsWithSequence
        )

        // Problem 3을 중간(position 1)으로 이동 - 1과 2 사이에는 중간값이 없음
        val reorderedPiece = piece.reorderProblem(problemId = 3L, targetPosition = 1)
        val sequences = reorderedPiece.getProblemsWithSequence().map { it.sequence }

        // 전체 재정렬로 10, 20, 30이 되어야 함
        assertThat(sequences).containsExactly(10, 20, 30)

        val problemIds = reorderedPiece.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(1L, 3L, 2L)
    }

    @Test
    fun `같은 위치로 이동`() {
        val piece = createTestPiece()
        // Problem 2를 현재 위치(position 1)로 다시 이동

        val reorderedPiece = piece.reorderProblem(problemId = 2L, targetPosition = 1)
        val sequences = reorderedPiece.getProblemsWithSequence().map { it.sequence }

        // 10과 30 사이의 중간값인 20이 사용되어야 함 (결과적으로 동일)
        assertThat(sequences).containsExactly(10, 20, 30, 40)

        val problemIds = reorderedPiece.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(1L, 2L, 3L, 4L)
    }

    @Test
    fun `존재하지 않는 문제 ID로 이동시 예외 발생`() {
        val piece = createTestPiece()

        assertThatThrownBy {
            piece.reorderProblem(problemId = 999L, targetPosition = 0)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Problem not found: 999")
    }

    @Test
    fun `잘못된 위치로 이동시 예외 발생`() {
        val piece = createTestPiece()

        // 음수 위치
        assertThatThrownBy {
            piece.reorderProblem(problemId = 1L, targetPosition = -1)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Invalid target position: -1")

        // 범위 초과 위치 (size는 4개이므로 3개 중에서 이동, 최대 position은 3)
        assertThatThrownBy {
            piece.reorderProblem(problemId = 1L, targetPosition = 4)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Invalid target position: 4")
    }

    @Test
    fun `빈 문제 리스트에서 이동시 예외 발생`() {
        val emptyPiece = Piece(
            pieceId = Piece.PieceId(1L),
            name = "빈 학습지",
            teacherId = 1L,
            problemsWithSequence = emptyList()
        )

        assertThatThrownBy {
            emptyPiece.reorderProblem(problemId = 1L, targetPosition = 0)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Cannot reorder empty problem list")
    }

    @Test
    fun `단일 문제에서 이동`() {
        val singleProblem = createProblem(1L)
        val piece = Piece(
            pieceId = Piece.PieceId(1L),
            name = "단일 문제 학습지",
            teacherId = 1L,
            problemsWithSequence = listOf(ProblemWithSequence(singleProblem, 10))
        )

        // 단일 문제를 position 0으로 이동 (결과적으로 동일)
        val reorderedPiece = piece.reorderProblem(problemId = 1L, targetPosition = 0)
        val sequences = reorderedPiece.getProblemsWithSequence().map { it.sequence }

        assertThat(sequences).containsExactly(10)
        assertThat(reorderedPiece.getProblemCount()).isEqualTo(1)
    }

    @Test
    fun `복잡한 시나리오 - 여러 번 이동`() {
        val piece = createTestPiece()

        // 1차: Problem 4를 맨 앞으로
        val step1 = piece.reorderProblem(problemId = 4L, targetPosition = 0)
        var problemIds = step1.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(4L, 1L, 2L, 3L)

        // 2차: Problem 1을 맨 뒤로  
        val step2 = step1.reorderProblem(problemId = 1L, targetPosition = 3)
        problemIds = step2.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(4L, 2L, 3L, 1L)

        // 3차: Problem 3을 position 1로
        val step3 = step2.reorderProblem(problemId = 3L, targetPosition = 1)
        problemIds = step3.getProblemsWithSequence().map { it.problem.id.value }
        assertThat(problemIds).containsExactly(4L, 3L, 2L, 1L)
    }
}
