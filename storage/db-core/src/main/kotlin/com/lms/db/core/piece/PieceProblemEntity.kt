package com.lms.db.core.piece

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "piece_problems")
class PieceProblemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "piece_id", nullable = false)
    val pieceId: Long,

    @Column(name = "problem_id", nullable = false)
    val problemId: Long,

    @Column(name = "sequence", nullable = false)
    val sequence: Int,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)