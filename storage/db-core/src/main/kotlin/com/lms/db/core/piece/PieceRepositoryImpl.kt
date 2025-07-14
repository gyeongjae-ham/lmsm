package com.lms.db.core.piece

import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.ProblemWithSequence
import com.lms.core_domain.piece.domain.repository.PieceRepository
import com.lms.db.core.problem.ProblemRepositoryImpl
import org.springframework.stereotype.Repository

@Repository
class PieceRepositoryImpl(
    private val pieceJpaRepository: PieceJpaRepository,
    private val pieceProblemJpaRepository: PieceProblemJpaRepository,
    private val problemRepositoryImpl: ProblemRepositoryImpl
) : PieceRepository {
    override fun savePiece(piece: Piece): Piece {
        val pieceEntity = piece.toEntity()

        val savedPieceEntity = if (pieceEntity.id != null) {
            pieceProblemJpaRepository.deleteByPieceId(pieceEntity.id!!)
            pieceEntity
        } else {
            pieceJpaRepository.save(pieceEntity)
        }

        val problemsWithSequence = piece.getProblemsWithSequence()
        val pieceProblemEntities = problemsWithSequence.map { problemWithSequence ->
            PieceProblemEntity(
                pieceId = savedPieceEntity.id!!,
                problemId = problemWithSequence.problem.id.value,
                sequence = problemWithSequence.sequence
            )
        }

        pieceProblemJpaRepository.saveAll(pieceProblemEntities)

        return Piece(
            pieceId = Piece.PieceId(savedPieceEntity.id!!),
            name = savedPieceEntity.name,
            teacherId = savedPieceEntity.teacherId,
            problemsWithSequence = problemsWithSequence
        )
    }

    override fun findById(pieceId: Piece.PieceId): Piece {
        val pieceEntity = pieceJpaRepository.findById(pieceId.value)
            .orElseThrow { BusinessException("Piece not found") }

        val pieceProblemEntities = pieceProblemJpaRepository.findByPieceIdOrderBySequence(pieceId.value)

        val problemIds = pieceProblemEntities.map { it.problemId }
        val problems = problemRepositoryImpl.findByIdIn(problemIds)

        val problemsWithSequence = pieceProblemEntities.map { pieceProblemEntity ->
            val problem = problems.getProblems().find { it.id.value == pieceProblemEntity.problemId }
                ?: throw IllegalStateException("Problem not found: ${pieceProblemEntity.problemId}")

            ProblemWithSequence(
                problem = problem,
                sequence = pieceProblemEntity.sequence
            )
        }

        return Piece(
            pieceId = pieceId,
            name = pieceEntity.name,
            teacherId = pieceEntity.teacherId,
            problemsWithSequence = problemsWithSequence
        )
    }
}
