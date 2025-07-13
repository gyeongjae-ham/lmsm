package com.lms.core_domain.piece

import com.lms.core_domain.exception.BusinessException
import com.lms.core_domain.problem.domain.Problem

private const val PROBLEM_MAX_SIZE = 50

class Piece(
    problemList: List<Problem>
) {
    private val problems: List<Problem>

    init {
        validateProblemList(problemList)
        problems = problemList.sortedWith(compareBy({ it.unitCode }, { it.level }))
    }

    private fun validateProblemList(problemList: List<Problem>) {
        if (problemList.isEmpty()) {
            throw BusinessException("Problem list cannot be empty")
        }

        if (problemList.size > PROBLEM_MAX_SIZE) {
            throw BusinessException("Piece cannot contain more than $PROBLEM_MAX_SIZE problems")
        }

        val duplicateProblems = problemList
            .groupingBy { it.id }
            .eachCount()
            .filter { it.value > 1 }

        if (duplicateProblems.isNotEmpty()) {
            throw BusinessException("Duplicate problems are not allowed in a piece")
        }
    }

    fun size(): Int {
        return problems.size
    }

    fun getProblems(): List<Problem> {
        return problems.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Piece) return false
        return problems == other.problems
    }

    override fun hashCode(): Int {
        return problems.hashCode()
    }
}
