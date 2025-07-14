package com.lms.db.core.studentpiece

import org.springframework.data.jpa.repository.JpaRepository

interface StudentPieceJpaRepository : JpaRepository<StudentPieceEntity, Long> {
    fun findByStudentIdInAndPieceId(studentIds: List<Long>, pieceId: Long): List<StudentPieceEntity>
}
