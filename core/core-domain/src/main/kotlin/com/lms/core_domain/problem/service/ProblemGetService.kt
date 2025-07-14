package com.lms.core_domain.problem.service

import com.lms.core_common.enum.ProblemType
import com.lms.core_domain.problem.domain.ProblemRatioCalculator
import com.lms.core_domain.problem.domain.ProblemSelector
import com.lms.core_domain.problem.domain.request.ProblemGetRequest
import com.lms.core_domain.problem.domain.response.ProblemFilterResponse
import com.lms.core_domain.problem.domain.toProblemFilterResponse
import com.lms.core_domain.problem.repository.ProblemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProblemGetService(
    private val problemRatioCalculator: ProblemRatioCalculator,
    private val problemRepository: ProblemRepository
) {
    @Transactional(readOnly = true)
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
