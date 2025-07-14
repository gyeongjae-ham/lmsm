package com.lms.core_domain.piece

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PieceTest {
    @Test
    fun `Problems로 Piece를 생성한다`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 2,
                answer = "1"
            )
        )
        val problems = Problems(problemList)

        val piece = Piece(
            name = "테스트 학습지",
            teacherId = 1L,
            problems = problems
        )

        assertThat(piece).isNotNull
        assertThat(piece.getName()).isEqualTo("테스트 학습지")
        assertThat(piece.getTeacherId()).isEqualTo(1L)
        assertThat(piece.getProblemCount()).isEqualTo(1)
    }

    @Test
    fun `Piece는 Problems를 unit_code와 level 순으로 정렬하고 sequence를 10, 20, 30 형태로 할당한다`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1583",
                level = 3,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(2),
                problemType = ProblemType.SUBJECTIVE,
                unitCode = "uc1580",
                level = 2,
                answer = "2"
            ),
            Problem(
                problemId = Problem.ProblemId(3),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "3"
            )
        )
        val problems = Problems(problemList)

        val piece = Piece(
            name = "정렬 테스트 학습지",
            teacherId = 1L,
            problems = problems
        )

        val problemsWithSequence = piece.getProblemsWithSequence()

        assertThat(problemsWithSequence).hasSize(3)

        // 정렬 순서 확인: uc1580(level1), uc1580(level2), uc1583(level3)
        assertThat(problemsWithSequence[0].problem.unitCode).isEqualTo("uc1580")
        assertThat(problemsWithSequence[0].problem.level).isEqualTo(1)
        assertThat(problemsWithSequence[0].sequence).isEqualTo(10)

        assertThat(problemsWithSequence[1].problem.unitCode).isEqualTo("uc1580")
        assertThat(problemsWithSequence[1].problem.level).isEqualTo(2)
        assertThat(problemsWithSequence[1].sequence).isEqualTo(20)

        assertThat(problemsWithSequence[2].problem.unitCode).isEqualTo("uc1583")
        assertThat(problemsWithSequence[2].problem.level).isEqualTo(3)
        assertThat(problemsWithSequence[2].sequence).isEqualTo(30)
    }

    @Test
    fun `Piece 생성 실패 - 최대 문제 수 초과`() {
        val problemList = (1..51).map { id ->
            Problem(
                problemId = Problem.ProblemId(id.toLong()),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            )
        }
        val problems = Problems(problemList)

        assertThatThrownBy {
            Piece(
                name = "초과 학습지",
                teacherId = 1L,
                problems = problems
            )
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Piece cannot contain more than 50 problems")
    }

    @Test
    fun `Piece 생성 실패 - 중복된 문제 ID`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(1), // 중복 ID
                problemType = ProblemType.SUBJECTIVE,
                unitCode = "uc1581",
                level = 2,
                answer = "2"
            )
        )
        val problems = Problems(problemList)

        assertThatThrownBy {
            Piece(
                name = "중복 학습지",
                teacherId = 1L,
                problems = problems
            )
        }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Duplicate problems are not allowed in a piece")
    }

    @Test
    fun `Piece 객체 동등성 비교`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            )
        )
        val problems1 = Problems(problemList)
        val problems2 = Problems(problemList)

        val piece1 = Piece(
            name = "동등성 테스트 학습지",
            teacherId = 1L,
            problems = problems1
        )
        val piece2 = Piece(
            name = "동등성 테스트 학습지",
            teacherId = 1L,
            problems = problems2
        )

        assertThat(piece1).isEqualTo(piece2)
        assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode())
    }

    @Test
    fun `최대 문제 수 50개로 Piece 생성 성공`() {
        val problemList = (1..50).map { id ->
            Problem(
                problemId = Problem.ProblemId(id.toLong()),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            )
        }
        val problems = Problems(problemList)

        val piece = Piece(
            name = "최대 크기 학습지",
            teacherId = 1L,
            problems = problems
        )

        assertThat(piece).isNotNull
        assertThat(piece.getProblemCount()).isEqualTo(50)
        assertThat(piece.getProblemsWithSequence()).hasSize(50)
        assertThat(piece.getProblemsWithSequence().last().sequence).isEqualTo(500)
    }
}
