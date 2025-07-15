package com.lms.db.core.studentanswer

import org.springframework.data.jpa.repository.JpaRepository

interface StudentAnswerJpaRepository : JpaRepository<StudentAnswerEntity, Long> {
    fun findByStudentIdAndPieceId(studentId: Long, pieceId: Long): List<StudentAnswerEntity>
}