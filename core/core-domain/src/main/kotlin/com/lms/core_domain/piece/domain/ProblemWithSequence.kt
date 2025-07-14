package com.lms.core_domain.piece.domain

import com.lms.core_domain.piece.domain.response.PieceAssignResponse
import com.lms.core_domain.piece.domain.response.ProblemWithSequenceResponse
import com.lms.core_domain.problem.domain.Problem

data class ProblemWithSequence(
    val problem: Problem,
    val sequence: Int
) {
    companion object {
        fun fromProblems(problems: List<Problem>): List<ProblemWithSequence> {
            return problems.mapIndexed { index, problem ->
                ProblemWithSequence(problem, (index + 1) * 10)
            }
        }
    }
}

fun ProblemWithSequence.toResponse(): ProblemWithSequenceResponse {
    return ProblemWithSequenceResponse(
        problemId = this.problem.id.value,
        orderIndex = this.sequence,
        unitCode = this.problem.unitCode,
        level = this.problem.level,
        problemType = this.problem.problemType,
        answer = this.problem.answer
    )
}

fun PieceAssignmentResult.toAssignResponse(pieceName: String): PieceAssignResponse {
    return PieceAssignResponse(
        pieceId = this.pieceId.value,
        pieceName = pieceName,
        assignedStudentCount = this.getAssignmentCount(),
        skippedStudentCount = this.getSkippedCount(),
    )
}

