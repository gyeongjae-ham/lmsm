package com.lms.core_domain.studentpiece.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentpiece.domain.repository.StudentPieceRepository
import com.lms.core_domain.user.domain.User
import org.springframework.stereotype.Service

@Service
class StudentPieceFinder(
    private val studentPieceRepository: StudentPieceRepository
) {
    fun findAssignedStudentIds(studentIds: List<User.UserId>, pieceId: Piece.PieceId): List<User.UserId> {
        return studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId)
            .map { it.getStudentId() }
    }
}
