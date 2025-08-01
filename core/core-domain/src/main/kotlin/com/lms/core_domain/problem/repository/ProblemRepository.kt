package com.lms.core_domain.problem.repository

import com.lms.core_common.enum.ProblemType
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems

interface ProblemRepository {
    fun getWithUnitCodeList(unitCodeList: List<String>): List<Problem>
    fun getWithUnitCodeListAndType(unitCodeList: List<String>, type: ProblemType): List<Problem>
    fun findByIdIn(problemIds: List<Long>): Problems
}
