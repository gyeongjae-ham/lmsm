package com.lms.core_domain.problem.service

import com.lms.core_domain.enum.Level
import com.lms.core_domain.enum.ProblemType
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.ProblemRatioCalculator
import com.lms.core_domain.problem.domain.RatioCalculateResult
import com.lms.core_domain.problem.domain.request.ProblemGetRequest
import com.lms.core_domain.problem.domain.response.ProblemFilterResponse
import com.lms.core_domain.problem.repository.ProblemRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProblemGetServiceTest {

    @MockK
    private lateinit var problemRatioCalculator: ProblemRatioCalculator

    @MockK
    private lateinit var problemRepository: ProblemRepository

    private lateinit var problemGetService: ProblemGetService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        problemGetService = ProblemGetService(problemRatioCalculator, problemRepository)
    }

    @Test
    fun `ProblemType이 ALL인 경우 모든 타입의 문제를 조회한다`() {
        val request = ProblemGetRequest(
            level = Level.LOW,
            totalCount = 10,
            unitCodeList = listOf("uc1580", "uc1581"),
            problemType = ProblemType.ALL
        )
        val ratioResult = RatioCalculateResult(lowCount = 3, mediumCount = 4, highCount = 3)
        val problemList = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(problemId = Problem.ProblemId(2), unitCode = "uc1580", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2"),
            Problem(problemId = Problem.ProblemId(3), unitCode = "uc1581", level = 5, problemType = ProblemType.SELECTION, answer = "3")
        )

        every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
        every { problemRepository.getWithUnitCodeList(listOf("uc1580", "uc1581")) } returns problemList

        val result = problemGetService.getProblemsWithCondition(request)

        assertThat(result).hasSize(3)
        assertThat(result).allMatch { it is ProblemFilterResponse }
        verify { problemRatioCalculator.calculate(Level.LOW, 10) }
        verify { problemRepository.getWithUnitCodeList(listOf("uc1580", "uc1581")) }
    }

    @Test
    fun `ProblemType이 SELECTION인 경우 선택형 문제만 조회한다`() {
        val request = ProblemGetRequest(
            level = Level.LOW,
            totalCount = 5,
            unitCodeList = listOf("uc1580"),
            problemType = ProblemType.SELECTION
        )
        val ratioResult = RatioCalculateResult(lowCount = 2, mediumCount = 2, highCount = 1)
        val problemList = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(problemId = Problem.ProblemId(2), unitCode = "uc1580", level = 2, problemType = ProblemType.SELECTION, answer = "2")
        )

        every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
        every { problemRepository.getWithUnitCodeListAndType(listOf("uc1580"), ProblemType.SELECTION) } returns problemList

        val result = problemGetService.getProblemsWithCondition(request)

        assertThat(result).hasSize(2)
        verify { problemRatioCalculator.calculate(Level.LOW, 5) }
        verify { problemRepository.getWithUnitCodeListAndType(listOf("uc1580"), ProblemType.SELECTION) }
    }

    @Test
    fun `ProblemType이 SUBJECTIVE인 경우 주관식 문제만 조회한다`() {
        val request = ProblemGetRequest(
            level = Level.LOW,
            totalCount = 3,
            unitCodeList = listOf("uc1580", "uc1581"),
            problemType = ProblemType.SUBJECTIVE
        )
        val ratioResult = RatioCalculateResult(lowCount = 3, mediumCount = 0, highCount = 0)
        val problemList = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 1, problemType = ProblemType.SUBJECTIVE, answer = "1"),
            Problem(problemId = Problem.ProblemId(2), unitCode = "uc1581", level = 1, problemType = ProblemType.SUBJECTIVE, answer = "2")
        )

        every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
        every { problemRepository.getWithUnitCodeListAndType(listOf("uc1580", "uc1581"), ProblemType.SUBJECTIVE) } returns problemList

        val result = problemGetService.getProblemsWithCondition(request)

        assertThat(result).hasSize(2)
        verify { problemRatioCalculator.calculate(Level.LOW, 3) }
        verify { problemRepository.getWithUnitCodeListAndType(listOf("uc1580", "uc1581"), ProblemType.SUBJECTIVE) }
    }

    @Test
    fun `unitCodeList에 공백이 있을 경우 trim하여 처리한다`() {
        val request = ProblemGetRequest(
            level = Level.MEDIUM,
            totalCount = 5,
            unitCodeList = listOf(" uc1580 ", "uc1581 ", " uc1582"),
            problemType = ProblemType.ALL
        )
        val ratioResult = RatioCalculateResult(lowCount = 2, mediumCount = 2, highCount = 1)
        val problemList = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 2, problemType = ProblemType.SELECTION, answer = "1")
        )

        every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
        every { problemRepository.getWithUnitCodeList(listOf("uc1580", "uc1581", "uc1582")) } returns problemList

        val result = problemGetService.getProblemsWithCondition(request)

        verify { problemRepository.getWithUnitCodeList(listOf("uc1580", "uc1581", "uc1582")) }
    }

    @Test
    fun `빈 문제 리스트가 반환되면 빈 결과를 반환한다`() {
        val request = ProblemGetRequest(
            level = Level.LOW,
            totalCount = 5,
            unitCodeList = listOf("uc1580"),
            problemType = ProblemType.ALL
        )
        val ratioResult = RatioCalculateResult(lowCount = 5, mediumCount = 0, highCount = 0)
        val problemList = emptyList<Problem>()

        every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
        every { problemRepository.getWithUnitCodeList(listOf("uc1580")) } returns problemList

        val result = problemGetService.getProblemsWithCondition(request)

        assertThat(result).isEmpty()
        verify { problemRatioCalculator.calculate(Level.LOW, 5) }
        verify { problemRepository.getWithUnitCodeList(listOf("uc1580")) }
    }

    @Nested
    inner class ProblemTypeBranchTest {

        @Test
        fun `ProblemType_isSame_ALL이_true일_때_getWithUnitCodeList_호출`() {
            val mockProblemType = mockk<ProblemType>()
            every { mockProblemType.isSame(ProblemType.ALL) } returns true

            val request = ProblemGetRequest(
                level = Level.MEDIUM,
                totalCount = 5,
                unitCodeList = listOf("uc1580"),
                problemType = mockProblemType
            )
            val ratioResult = RatioCalculateResult(lowCount = 5, mediumCount = 0, highCount = 0)
            val problemList = listOf(
                Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1")
            )

            every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
            every { problemRepository.getWithUnitCodeList(listOf("uc1580")) } returns problemList

            problemGetService.getProblemsWithCondition(request)

            verify { problemRepository.getWithUnitCodeList(listOf("uc1580")) }
            verify(exactly = 0) { problemRepository.getWithUnitCodeListAndType(any(), any()) }
        }

        @Test
        fun `ProblemType_isSame_ALL이_false일_때_getWithUnitCodeListAndType_호출`() {
            val mockProblemType = mockk<ProblemType>()
            every { mockProblemType.isSame(ProblemType.ALL) } returns false

            val request = ProblemGetRequest(
                level = Level.MEDIUM,
                totalCount = 5,
                unitCodeList = listOf("uc1580"),
                problemType = mockProblemType
            )
            val ratioResult = RatioCalculateResult(lowCount = 5, mediumCount = 0, highCount = 0)
            val problemList = listOf(
                Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1")
            )

            every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
            every { problemRepository.getWithUnitCodeListAndType(listOf("uc1580"), mockProblemType) } returns problemList

            problemGetService.getProblemsWithCondition(request)

            verify { problemRepository.getWithUnitCodeListAndType(listOf("uc1580"), mockProblemType) }
            verify(exactly = 0) { problemRepository.getWithUnitCodeList(any()) }
        }
    }

    @Test
    fun `Problems 객체가 올바르게 생성되고 extract가 호출된다`() {
        val request = ProblemGetRequest(
            level = Level.MEDIUM,
            totalCount = 4,
            unitCodeList = listOf("uc1580"),
            problemType = ProblemType.ALL
        )
        val ratioResult = RatioCalculateResult(lowCount = 2, mediumCount = 1, highCount = 1)
        val problemList = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(problemId = Problem.ProblemId(2), unitCode = "uc1580", level = 2, problemType = ProblemType.SELECTION, answer = "2"),
            Problem(problemId = Problem.ProblemId(3), unitCode = "uc1580", level = 5, problemType = ProblemType.SELECTION, answer = "3")
        )

        every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
        every { problemRepository.getWithUnitCodeList(listOf("uc1580")) } returns problemList

        val result = problemGetService.getProblemsWithCondition(request)

        assertThat(result).isNotEmpty()
        assertThat(result).allMatch { it is ProblemFilterResponse }
        verify { problemRatioCalculator.calculate(Level.MEDIUM, 4) }
        verify { problemRepository.getWithUnitCodeList(listOf("uc1580")) }
    }

    @Test
    fun `복수의 unitCode와 다양한 레벨의 문제가 올바르게 처리된다`() {
        val request = ProblemGetRequest(
            level = Level.MEDIUM,
            totalCount = 6,
            unitCodeList = listOf("uc1580", "uc1581", "uc1582"),
            problemType = ProblemType.SELECTION
        )
        val ratioResult = RatioCalculateResult(lowCount = 2, mediumCount = 2, highCount = 2)
        val problemList = listOf(
            Problem(problemId = Problem.ProblemId(1), unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(problemId = Problem.ProblemId(2), unitCode = "uc1581", level = 2, problemType = ProblemType.SELECTION, answer = "2"),
            Problem(problemId = Problem.ProblemId(3), unitCode = "uc1582", level = 5, problemType = ProblemType.SELECTION, answer = "3"),
            Problem(problemId = Problem.ProblemId(4), unitCode = "uc1580", level = 3, problemType = ProblemType.SELECTION, answer = "4"),
            Problem(problemId = Problem.ProblemId(5), unitCode = "uc1581", level = 4, problemType = ProblemType.SELECTION, answer = "5"),
            Problem(problemId = Problem.ProblemId(6), unitCode = "uc1582", level = 1, problemType = ProblemType.SELECTION, answer = "1")
        )

        every { problemRatioCalculator.calculate(any(), any()) } returns ratioResult
        every { problemRepository.getWithUnitCodeListAndType(listOf("uc1580", "uc1581", "uc1582"), ProblemType.SELECTION) } returns problemList

        val result = problemGetService.getProblemsWithCondition(request)

        assertThat(result).hasSize(5)
        verify { problemRatioCalculator.calculate(Level.MEDIUM, 6) }
        verify { problemRepository.getWithUnitCodeListAndType(listOf("uc1580", "uc1581", "uc1582"), ProblemType.SELECTION) }
    }
}
