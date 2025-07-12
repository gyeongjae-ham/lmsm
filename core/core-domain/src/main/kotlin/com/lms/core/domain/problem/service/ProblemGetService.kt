package com.lms.core.domain.problem.service

import com.lms.core.domain.problem.domain.problem.ProblemRatioCalculator
import com.lms.core.domain.problem.domain.problem.request.ProblemGetRequest
import com.lms.core.domain.problem.repository.ProblemRepository
import com.lms.core.enum.ProblemType
import org.springframework.stereotype.Service

@Service
class ProblemGetService(
    private val problemRatioCalculator: ProblemRatioCalculator,
    private val problemRepository: ProblemRepository
) {
    fun getProblemsWithCondition(request: ProblemGetRequest) {
        val calculationResult = problemRatioCalculator.calculate(request.level, request.totalCount)
        val timedUnitCodeList = request.unitCodeList.map { unitCode -> unitCode.trim() }

        val problems = if (request.problemType.isSame(ProblemType.ALL)) {
            problemRepository.getWithUnitCodeList(unitCodeList = timedUnitCodeList)
        } else {
            problemRepository.getWithUnitCodeListAndType(unitCodeList = timedUnitCodeList, type = request.problemType)
        }

        println(problems.toString())
    }
}
