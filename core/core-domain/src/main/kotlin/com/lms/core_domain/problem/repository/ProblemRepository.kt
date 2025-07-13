package com.lms.core_domain.problem.repository

import com.lms.core_domain.enum.ProblemType
import com.lms.core_domain.problem.domain.Problem

interface ProblemRepository {
    fun getWithUnitCodeList(unitCodeList: List<String>): List<Problem>
    fun getWithUnitCodeListAndType(unitCodeList: List<String>, type: ProblemType): List<Problem>
}
