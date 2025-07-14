package com.lms.core_domain.piece.domain.repository

import com.lms.core_domain.piece.domain.Piece

interface PieceRepository {
    fun savePiece(piece: Piece): Piece
}
