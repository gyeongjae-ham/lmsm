package com.lms.core_domain.piece

import com.lms.core_domain.enum.ProblemType
import com.lms.core_domain.exception.BusinessException
import com.lms.core_domain.problem.domain.Problem
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test

class PieceTest {
    @Test
    fun `Problem의 List로 Piece를 생성한다`() {
        val problems = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                unitCode = "uxe999",
                level = 3,
                problemType = ProblemType.SELECTION,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(2),
                unitCode = "uxe888",
                level = 1,
                problemType = ProblemType.SUBJECTIVE,
                answer = "2"
            )
        )

        val piece = Piece(problemList = problems)

        assertThat(piece).isNotNull()
        assertThat(piece.size()).isEqualTo(2)
    }

    @Test
    fun `빈 Problem List로 Piece를 생성하면 Exception이 발생한다`() {
        val emptyProblems = emptyList<Problem>()

        val piece =
            assertThatThrownBy { Piece(problemList = emptyProblems) }
                .isInstanceOf(BusinessException::class.java)
                .hasMessage("Problem list cannot be empty")
    }

    @Test
    fun `최대 문제 수 제한을 검증한다`() {
        val problems = (1..51).map {
            Problem(
                problemId = Problem.ProblemId(it.toLong()),
                unitCode = "unit_$it",
                level = (it % 5) + 1,
                problemType = if (it % 2 == 0) ProblemType.SELECTION else ProblemType.SUBJECTIVE,
                answer = it.toString()
            )
        }

        assertThatThrownBy { Piece(problemList = problems) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Piece cannot contain more than 50 problems")
    }

    @Test
    fun `중복된 문제로 학습지 생성 시 예외가 발생한다`() {
        val duplicateProblems = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "uxe999", level = 3, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(problemId = Problem.ProblemId(1), unitCode = "uxe999", level = 3, problemType = ProblemType.SELECTION, answer = "1")
        )

        assertThatThrownBy { Piece(problemList = duplicateProblems) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Duplicate problems are not allowed in a piece")
    }

    @Test
    fun `문제들이 unit_code와 level 순서로 정렬된다`() {
        val problems = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "z_unit", level = 3, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(problemId = Problem.ProblemId(2), unitCode = "a_unit", level = 2, problemType = ProblemType.SELECTION, answer = "2"),
            Problem(problemId = Problem.ProblemId(3), unitCode = "a_unit", level = 1, problemType = ProblemType.SELECTION, answer = "3")
        )

        val piece = Piece(problemList = problems)

        assertThat(piece.getProblems()).extracting("unitCode", "level")
            .containsExactly(
                tuple("a_unit", 1),
                tuple("a_unit", 2),
                tuple("z_unit", 3)
            )
    }
}
