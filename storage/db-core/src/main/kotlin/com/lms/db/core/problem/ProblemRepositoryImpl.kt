package com.lms.db.core.problem

import com.lms.core_common.enum.ProblemType
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
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

    override fun findByIdIn(problemIds: List<Long>): Problems {
        val problemEntities = problemJpaRepository.findAllByIdIn(problemIds)
        return Problems(problemEntities.map { it.toDomain() })
    }
}
