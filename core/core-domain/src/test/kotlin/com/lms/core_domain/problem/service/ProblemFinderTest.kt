package com.lms.core_domain.problem.service

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
import com.lms.core_domain.problem.repository.ProblemRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProblemFinderTest {

    @MockK
    private lateinit var problemRepository: ProblemRepository

    @InjectMockKs
    private lateinit var problemFinder: ProblemFinder

    @Test
    fun `문제 ID 목록이 비어있으면 BusinessException을 던진다`() {
        val emptyIds = emptyList<Long>()

        assertThatThrownBy { problemFinder.getProblemsForPiece(emptyIds) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Problems can not be empty")
    }

    @Test
    fun `일부 문제를 찾을 수 없으면 BusinessException을 던진다`() {
        val problemIds = listOf(1L, 2L, 3L)
        val foundProblems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                )
            )
        )

        every { problemRepository.findByIdIn(problemIds) } returns foundProblems

        assertThatThrownBy { problemFinder.getProblemsForPiece(problemIds) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Some problems could not be found")
    }

    @Test
    fun `모든 문제를 성공적으로 찾으면 Problems를 반환한다`() {
        val problemIds = listOf(1L, 2L)
        val foundProblems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                )
            )
        )

        every { problemRepository.findByIdIn(problemIds) } returns foundProblems

        val result = problemFinder.getProblemsForPiece(problemIds)

        assertThat(result).isEqualTo(foundProblems)
        assertThat(result.size()).isEqualTo(2)
        verify { problemRepository.findByIdIn(problemIds) }
    }

    @Test
    fun `단일 문제 ID로 조회가 성공한다`() {
        val problemIds = listOf(1L)
        val foundProblems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                )
            )
        )

        every { problemRepository.findByIdIn(problemIds) } returns foundProblems

        val result = problemFinder.getProblemsForPiece(problemIds)

        assertThat(result).isEqualTo(foundProblems)
        assertThat(result.size()).isEqualTo(1)
        verify { problemRepository.findByIdIn(problemIds) }
    }

    @Test
    fun `Repository의 findByIdIn 메소드가 정확히 한 번 호출된다`() {
        val problemIds = listOf(1L, 2L, 3L)
        val foundProblems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                ),
                Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1583",
                    level = 3,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                )
            )
        )

        every { problemRepository.findByIdIn(problemIds) } returns foundProblems

        problemFinder.getProblemsForPiece(problemIds)

        verify(exactly = 1) { problemRepository.findByIdIn(problemIds) }
    }
}
