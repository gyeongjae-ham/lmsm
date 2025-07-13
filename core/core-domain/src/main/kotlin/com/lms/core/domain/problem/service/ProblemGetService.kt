package com.lms.core.domain.problem.service

import com.lms.core.domain.problem.domain.problem.ProblemRatioCalculator
import com.lms.core.domain.problem.domain.problem.ProblemSelector
import com.lms.core.domain.problem.domain.problem.request.ProblemGetRequest
import com.lms.core.domain.problem.domain.problem.response.ProblemFilterResponse
import com.lms.core.domain.problem.domain.problem.toProblemFilterResponse
import com.lms.core.domain.problem.repository.ProblemRepository
import com.lms.core.enum.ProblemType
import org.springframework.stereotype.Service

@Service
class ProblemGetService(
    private val problemRatioCalculator: ProblemRatioCalculator,
    private val problemRepository: ProblemRepository
) {
    fun getProblemsWithCondition(request: ProblemGetRequest): List<ProblemFilterResponse> {
        val ratioResult = problemRatioCalculator.calculate(request.level, request.totalCount)
        val trimmedUnitCodeList = request.unitCodeList.map { unitCode -> unitCode.trim() }

        val problemList = if (request.problemType.isSame(ProblemType.ALL)) {
            problemRepository.getWithUnitCodeList(unitCodeList = trimmedUnitCodeList)
        } else {
            problemRepository.getWithUnitCodeListAndType(unitCodeList = trimmedUnitCodeList, type = request.problemType)
        }

        val problemSelector = ProblemSelector(
            ratioResult = ratioResult,
            problems = problemList
        )

        return problemSelector.extract().map { it.toProblemFilterResponse() }
    }
}
