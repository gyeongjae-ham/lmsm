package com.lms.core_domain.problem.service

import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.problem.domain.Problems
import com.lms.core_domain.problem.repository.ProblemRepository
import org.springframework.stereotype.Service

@Service
class ProblemFinder(
    private val problemRepository: ProblemRepository
) {
    fun getProblemsForPiece(problemIds: List<Long>): Problems {
        if (problemIds.isEmpty()) {
            throw BusinessException("Problems can not be empty")
        }

        val problems = problemRepository.findByIdIn(problemIds)
        if (!problems.isSameSize(problemIds.size)) {
            throw BusinessException("Some problems could not be found")
        }
        return problems
    }
}
