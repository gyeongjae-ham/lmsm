package com.lms.core_domain.piece.domain

import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.response.PieceCreateResponse
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems

private const val PROBLEM_MAX_SIZE = 50

class Piece(
    private val pieceId: PieceId? = null,
    private val name: String,
    private val teacherId: Long,
    problems: Problems
) {
    val id: PieceId
        get() = requireNotNull(pieceId) { BusinessException("Piece ID is null.") }

    private val sortedProblemsWithSequence: List<ProblemWithSequence>

    init {
        validateProblemList(problems)
        val sortedProblems = problems.sortedWith(compareBy<Problem> { it.unitCode }.thenBy { it.level })
        sortedProblemsWithSequence = ProblemWithSequence.fromProblems(sortedProblems.getProblems())
    }

    fun getName(): String = name
    fun getTeacherId(): Long = teacherId
    fun getProblemCount(): Int = sortedProblemsWithSequence.size
    fun getProblemsWithSequence(): List<ProblemWithSequence> = sortedProblemsWithSequence.toList()

    private fun validateProblemList(problems: Problems) {
        if (problems.isWithinMaxSize(PROBLEM_MAX_SIZE)) {
            throw BusinessException("Piece cannot contain more than $PROBLEM_MAX_SIZE problems")
        }

        if (problems.hasDuplicated()) {
            throw BusinessException("Duplicate problems are not allowed in a piece")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Piece) return false

        if (teacherId != other.teacherId) return false
        if (pieceId != other.pieceId) return false
        if (name != other.name) return false
        if (sortedProblemsWithSequence != other.sortedProblemsWithSequence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = teacherId.hashCode()
        result = 31 * result + (pieceId?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + sortedProblemsWithSequence.hashCode()
        return result
    }

    data class PieceId(val value: Long)
}

fun Piece.toCreateResponse(): PieceCreateResponse {
    return PieceCreateResponse(
        id = this.id.value,
        name = this.getName(),
        teacherId = this.getTeacherId(),
        problemCount = this.getProblemCount()
    )
}
