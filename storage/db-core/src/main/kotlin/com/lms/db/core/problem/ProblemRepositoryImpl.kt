package com.lms.db.core.problem

import com.lms.core_domain.enum.ProblemType
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.repository.ProblemRepository
import org.springframework.stereotype.Repository

@Repository
class ProblemRepositoryImpl(
    private val problemJpaRepository: ProblemJpaRepository
) : ProblemRepository {
    override fun getWithUnitCodeList(unitCodeList: List<String>): List<Problem> {
        return problemJpaRepository.findAllByUnitCodeIn(unitCodeList = unitCodeList)
            .map { it.toDomain() }
    }

    override fun getWithUnitCodeListAndType(unitCodeList: List<String>, type: ProblemType): List<Problem> {
        return problemJpaRepository.findAllByProblemTypeAndUnitCodeIn(problemType = type, unitCodeList = unitCodeList)
            .map { it.toDomain() }
    }
}
