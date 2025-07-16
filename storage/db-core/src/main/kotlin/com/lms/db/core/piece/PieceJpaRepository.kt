package com.lms.db.core.piece

import org.springframework.data.jpa.repository.JpaRepository

interface PieceJpaRepository : JpaRepository<PieceEntity, Long> {
}
