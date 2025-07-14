package com.lms.core_domain.problem.domain

import com.lms.core_common.exception.BusinessException

class Problems(
    private val problemList: List<Problem>
) {
    init {
        if (problemList.isEmpty()) {
            throw BusinessException("Problems can not be empty")
        }
    }

    fun size(): Int {
        return problemList.size
    }

    fun isSameSize(size: Int): Boolean {
        return size == size()
    }

    fun isWithinMaxSize(problemMaxSize: Int): Boolean {
        return size() > problemMaxSize
    }

    fun sortedWith(comparator: Comparator<Problem>): Problems {
        val sortedProblemList = problemList.sortedWith(comparator)
        return Problems(sortedProblemList)
    }

    fun hasDuplicated(): Boolean {
        val duplicateProblems = problemList
            .groupingBy { it.id }
            .eachCount()
            .filter { it.value > 1 }

        return duplicateProblems.isNotEmpty()
    }
    
    fun getProblems(): List<Problem> {
        return problemList.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Problems) return false

        if (problemList != other.problemList) return false

        return true
    }

    override fun hashCode(): Int {
        return problemList.hashCode()
    }
}
