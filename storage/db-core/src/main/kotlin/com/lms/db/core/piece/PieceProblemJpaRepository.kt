package com.lms.db.core.piece

import org.springframework.data.jpa.repository.JpaRepository

interface PieceProblemJpaRepository : JpaRepository<PieceProblemEntity, Long> {
    fun findByPieceIdOrderBySequence(pieceId: Long): List<PieceProblemEntity>
    fun deleteByPieceId(pieceId: Long)
}