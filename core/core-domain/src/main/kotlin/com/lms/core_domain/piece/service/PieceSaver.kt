package com.lms.core_domain.piece.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.repository.PieceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PieceSaver(
    private val pieceRepository: PieceRepository
) {
    fun savePiece(piece: Piece): Piece {
        return pieceRepository.savePiece(piece)
    }
}
