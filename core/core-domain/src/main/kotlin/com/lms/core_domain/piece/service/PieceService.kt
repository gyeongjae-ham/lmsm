package com.lms.core_domain.piece.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.request.PieceCreateRequest
import com.lms.core_domain.piece.domain.response.PieceCreateResponse
import com.lms.core_domain.piece.domain.toCreateResponse
import com.lms.core_domain.problem.service.ProblemFinder
import org.springframework.stereotype.Service

@Service
class PieceService(
    private val problemFinder: ProblemFinder,
    private val pieceSaver: PieceSaver,
) {
    fun create(request: PieceCreateRequest): PieceCreateResponse {
        val problems = problemFinder.getProblemsForPiece(problemIds = request.problemIds)
        val piece = Piece(
            problems = problems,
            name = request.name,
            teacherId = request.teacherId,
        )
        val savedPiece = pieceSaver.savePiece(piece)
        return savedPiece.toCreateResponse()
    }
}
