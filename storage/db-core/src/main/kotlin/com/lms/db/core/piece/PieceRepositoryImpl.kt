package com.lms.db.core.piece

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.repository.PieceRepository
import com.lms.core_domain.problem.domain.Problems
import org.springframework.stereotype.Repository

@Repository
class PieceRepositoryImpl(
    private val pieceJpaRepository: PieceJpaRepository,
    private val pieceProblemJpaRepository: PieceProblemJpaRepository
) : PieceRepository {
    override fun savePiece(piece: Piece): Piece {
        val pieceEntity = piece.toEntity()
        val savedPieceEntity = pieceJpaRepository.save(pieceEntity)

        val problemsWithSequence = piece.getProblemsWithSequence()
        val pieceProblemEntities = problemsWithSequence.map { problemWithSequence ->
            PieceProblemEntity(
                pieceId = savedPieceEntity.id!!,
                problemId = problemWithSequence.problem.id.value,
                sequence = problemWithSequence.sequence
            )
        }

        pieceProblemJpaRepository.saveAll(pieceProblemEntities)

        val problems = problemsWithSequence.map { it.problem }
        return Piece(
            pieceId = Piece.PieceId(savedPieceEntity.id!!),
            name = savedPieceEntity.name,
            teacherId = savedPieceEntity.teacherId,
            problems = Problems(problems)
        )
    }
}
