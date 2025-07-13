package com.lms.core.domain.problem.repository

import com.lms.core.domain.problem.domain.problem.Problem
import com.lms.core.enum.ProblemType

interface ProblemRepository {
    fun getWithUnitCodeList(unitCodeList: List<String>): List<Problem>
    fun getWithUnitCodeListAndType(unitCodeList: List<String>, type: ProblemType): List<Problem>
}
