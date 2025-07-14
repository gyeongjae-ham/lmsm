package com.lms.core_domain.piece.domain

import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.response.PieceCreateResponse
import com.lms.core_domain.piece.domain.response.ProblemOrderResponse
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems

private const val PROBLEM_MAX_SIZE = 50

class Piece {
    private val pieceId: PieceId?
    private val name: String
    private val teacherId: Long
    private val sortedProblemsWithSequence: List<ProblemWithSequence>

    val id: PieceId
        get() = requireNotNull(pieceId) { BusinessException("Piece ID is null.") }

    constructor(
        pieceId: PieceId? = null,
        name: String,
        teacherId: Long,
        problems: Problems
    ) {
        this.pieceId = pieceId
        this.name = name
        this.teacherId = teacherId

        validateProblemList(problems)
        val sortedProblems = problems.sortedWith(compareBy<Problem> { it.unitCode }.thenBy { it.level })
        this.sortedProblemsWithSequence = ProblemWithSequence.fromProblems(sortedProblems.getProblems())
    }

    constructor(
        pieceId: PieceId,
        name: String,
        teacherId: Long,
        problemsWithSequence: List<ProblemWithSequence>
    ) {
        this.pieceId = pieceId
        this.name = name
        this.teacherId = teacherId
        this.sortedProblemsWithSequence = problemsWithSequence
    }

    fun getName(): String = name
    fun getTeacherId(): Long = teacherId
    fun getProblemCount(): Int = sortedProblemsWithSequence.size
    fun getProblemsWithSequence(): List<ProblemWithSequence> = sortedProblemsWithSequence.toList()

    fun reorderProblem(problemId: Long, targetPosition: Int): Piece {
        if (sortedProblemsWithSequence.isEmpty()) {
            throw BusinessException("Cannot reorder empty problem list")
        }

        val movingProblem = sortedProblemsWithSequence.find { it.problem.id.value == problemId }
            ?: throw BusinessException("Problem not found: $problemId")

        val otherProblems = sortedProblemsWithSequence.filter { it.problem.id.value != problemId }
        val sortedOthers = otherProblems.sortedBy { it.sequence }

        if (targetPosition < 0 || targetPosition > sortedOthers.size) {
            throw BusinessException("Invalid target position: $targetPosition")
        }

        val reorderedProblems = calculateNewSequence(sortedOthers, movingProblem, targetPosition)

        return Piece(
            pieceId = this.id,
            name = this.name,
            teacherId = this.teacherId,
            problemsWithSequence = reorderedProblems
        )
    }

    private fun calculateNewSequence(
        sortedOthers: List<ProblemWithSequence>,
        movingProblem: ProblemWithSequence,
        targetPosition: Int
    ): List<ProblemWithSequence> {
        val prevSequence = if (targetPosition == 0) 0 else sortedOthers[targetPosition - 1].sequence
        val nextSequence = if (targetPosition >= sortedOthers.size) null else sortedOthers[targetPosition].sequence

        return when {
            // 맨 뒤에 삽입
            nextSequence == null -> {
                val newSequence = prevSequence + 10
                val updatedMovingProblem = movingProblem.copy(sequence = newSequence)
                (sortedOthers + updatedMovingProblem).sortedBy { it.sequence }
            }

            // 중간값 사용 가능한지 확인
            else -> {
                val gap = nextSequence - prevSequence
                val midPoint = prevSequence + gap / 2

                if (midPoint > prevSequence && midPoint < nextSequence) {
                    // 중간값 사용
                    val updatedMovingProblem = movingProblem.copy(sequence = midPoint)
                    (sortedOthers + updatedMovingProblem).sortedBy { it.sequence }
                } else {
                    // 전체 재정렬
                    val allProblems = sortedOthers.toMutableList()
                    allProblems.add(targetPosition, movingProblem)
                    allProblems.mapIndexed { index, problem ->
                        problem.copy(sequence = (index + 1) * 10)
                    }
                }
            }
        }
    }

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

fun Piece.toUpdateOrderResponse(): ProblemOrderResponse {
    return ProblemOrderResponse(
        pieceId = this.id.value,
        name = this.getName(),
        problemCount = this.getProblemCount(),
        problems = this.getProblemsWithSequence().map { it.toResponse() }
    )
}
