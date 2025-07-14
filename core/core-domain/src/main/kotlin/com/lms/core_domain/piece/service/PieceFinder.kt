package com.lms.core_domain.piece.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.repository.PieceRepository
import org.springframework.stereotype.Service

@Service
class PieceFinder(
    private val pieceRepository: PieceRepository
) {
    fun getWithId(pieceId: Piece.PieceId): Piece {
        return pieceRepository.findById(pieceId)
    }
}
