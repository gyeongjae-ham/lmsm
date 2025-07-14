package com.lms.db.core.studentpiece

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentpiece.domain.StudentPiece
import com.lms.core_domain.studentpiece.domain.repository.StudentPieceRepository
import com.lms.core_domain.user.domain.User
import org.springframework.stereotype.Repository

@Repository
class StudentPieceRepositoryImpl(
    private val studentPieceJpaRepository: StudentPieceJpaRepository
) : StudentPieceRepository {

    override fun saveAll(studentPieces: List<StudentPiece>): List<StudentPiece> {
        val entities = studentPieces.map { it.toEntity() }
        val savedEntities = studentPieceJpaRepository.saveAll(entities)
        return savedEntities.map { it.toDomain() }
    }

    override fun findByStudentIdsAndPieceId(studentIds: List<User.UserId>, pieceId: Piece.PieceId): List<StudentPiece> {
        val ids = studentIds.map { it.value }
        return studentPieceJpaRepository.findByStudentIdInAndPieceId(ids, pieceId.value)
            .map { it.toDomain() }
    }
}

fun StudentPiece.toEntity(): StudentPieceEntity {
    return StudentPieceEntity(
        id = try {
            this.id.value
        } catch (e: Exception) {
            null
        },
        studentId = this.getStudentId().value,
        pieceId = this.getPieceId().value
    )
}

fun StudentPieceEntity.toDomain(): StudentPiece {
    return StudentPiece(
        studentPieceId = this.id?.let { StudentPiece.StudentPieceId(it) },
        studentId = User.UserId(this.studentId),
        pieceId = Piece.PieceId(this.pieceId)
    )
}
