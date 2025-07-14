package com.lms.core_domain.problem.domain

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProblemsTest {
    @Test
    fun `Problem List로 Problems를 생성한다`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(2),
                problemType = ProblemType.SELECTION,
                unitCode = "nn1111",
                level = 2,
                answer = "1"
            )
        )

        val problems = Problems(problemList = problemList)

        assertThat(problems.size()).isEqualTo(1)
    }

    @Test
    fun `빈 문제 리스트로 Problems 생성 시 예외 발생`() {
        val problemList = emptyList<Problem>()

        Assertions.assertThatThrownBy { Problems(problemList) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Problems can not be empty")
    }

    @Test
    fun `Problems 크기 확인`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(2),
                problemType = ProblemType.SUBJECTIVE,
                unitCode = "uc1581",
                level = 2,
                answer = "2"
            )
        )
        val problems = Problems(problemList)

        assertThat(problems.size()).isEqualTo(2)
    }

    @Test
    fun `Problems 크기 비교 - 같은 크기`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(2),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1581",
                level = 2,
                answer = "2"
            )
        )
        val problems = Problems(problemList)

        assertThat(problems.isSameSize(2)).isTrue()
        assertThat(problems.isSameSize(3)).isFalse()
    }

    @Test
    fun `Problems 최대 크기 확인 - 최대 크기를 초과하지 않음`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            )
        )
        val problems = Problems(problemList)

        assertThat(problems.isWithinMaxSize(50)).isFalse()
        assertThat(problems.isWithinMaxSize(1)).isFalse()
        assertThat(problems.isWithinMaxSize(0)).isTrue()
    }

    @Test
    fun `Problems 정렬 - unit_code와 level 순으로 정렬`() {
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

        val sortedProblems = problems.sortedWith(compareBy<Problem> { it.unitCode }.thenBy { it.level })

        assertThat(sortedProblems.size()).isEqualTo(3)
        assertThat(sortedProblems).isNotSameAs(problems)
    }

    @Test
    fun `Problems 중복 확인 - 중복 없음`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(2),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1581",
                level = 2,
                answer = "2"
            )
        )
        val problems = Problems(problemList)

        assertThat(problems.hasDuplicated()).isFalse()
    }

    @Test
    fun `Problems 중복 확인 - 중복 있음`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SUBJECTIVE,
                unitCode = "uc1581",
                level = 2,
                answer = "2"
            )
        )
        val problems = Problems(problemList)

        assertThat(problems.hasDuplicated()).isTrue()
    }

    @Test
    fun `Problems 객체 동등성 비교`() {
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

        assertThat(problems1).isEqualTo(problems2)
        assertThat(problems1.hashCode()).isEqualTo(problems2.hashCode())
    }

    @Test
    fun `Problems 정렬 - 역순 정렬`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1580",
                level = 1,
                answer = "1"
            ),
            Problem(
                problemId = Problem.ProblemId(2),
                problemType = ProblemType.SELECTION,
                unitCode = "uc1581",
                level = 2,
                answer = "2"
            )
        )
        val problems = Problems(problemList)

        val reverseSortedProblems = problems.sortedWith(compareByDescending { it.level })

        assertThat(reverseSortedProblems.size()).isEqualTo(2)
        assertThat(reverseSortedProblems).isNotSameAs(problems)
    }
}
