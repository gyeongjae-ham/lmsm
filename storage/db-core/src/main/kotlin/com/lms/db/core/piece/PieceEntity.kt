package com.lms.db.core.piece

import com.lms.core_domain.piece.domain.Piece
import com.lms.db.core.config.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "pieces")
class PieceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "teacher_id", nullable = false)
    val teacherId: Long
) : BaseEntity() {
}

fun Piece.toEntity(): PieceEntity {
    return PieceEntity(
        name = this.getName(),
        teacherId = this.getTeacherId(),
    )
}
