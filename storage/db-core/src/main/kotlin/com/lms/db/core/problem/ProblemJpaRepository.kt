package com.lms.db.core.problem

import com.lms.core.enum.ProblemType
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemJpaRepository : JpaRepository<ProblemEntity, Long> {
    fun findAllByUnitCodeIn(unitCodeList: List<String>): List<ProblemEntity>
    fun findAllByProblemTypeAndUnitCodeIn(problemType: ProblemType, unitCodeList: List<String>): List<ProblemEntity>
}
