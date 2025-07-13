package com.lms.db.core.problem

import com.lms.core_domain.enum.ProblemType
import com.lms.core_domain.problem.domain.Problem
import com.lms.db.core.config.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "problems",
    indexes = [
        Index(name = "idx_problem_unit_code", columnList = "unit_code"),
        Index(name = "idx_problem_level", columnList = "level"),
        Index(name = "idx_problem_type", columnList = "problem_type")
    ]
)
class ProblemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "unit_code", nullable = false, length = 20)
    val unitCode: String,

    @Column(name = "level", nullable = false)
    val level: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type", nullable = false, length = 20)
    val problemType: ProblemType,

    @Column(name = "answer", nullable = false, length = 500)
    val answer: String
) : BaseEntity() {
    fun toDomain(): Problem {
        return Problem(
            problemId = Problem.ProblemId(id),
            unitCode = unitCode,
            level = level,
            problemType = problemType,
            answer = answer,
        )
    }
}
