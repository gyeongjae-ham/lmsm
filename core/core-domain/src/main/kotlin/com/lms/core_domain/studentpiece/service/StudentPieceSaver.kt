package com.lms.core_domain.studentpiece.service

import com.lms.core_domain.studentpiece.domain.StudentPiece
import com.lms.core_domain.studentpiece.domain.repository.StudentPieceRepository
import org.springframework.stereotype.Service

@Service
class StudentPieceSaver(
    private val studentPieceRepository: StudentPieceRepository
) {
    fun saveAll(studentPieces: List<StudentPiece>): List<StudentPiece> {
        return studentPieceRepository.saveAll(studentPieces)
    }
}
