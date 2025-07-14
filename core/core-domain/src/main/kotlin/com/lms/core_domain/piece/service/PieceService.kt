package com.lms.core_domain.piece.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.request.PieceAssignRequest
import com.lms.core_domain.piece.domain.request.PieceCreateRequest
import com.lms.core_domain.piece.domain.request.ProblemOrderUpdateRequest
import com.lms.core_domain.piece.domain.response.PieceAssignResponse
import com.lms.core_domain.piece.domain.response.PieceCreateResponse
import com.lms.core_domain.piece.domain.response.ProblemOrderResponse
import com.lms.core_domain.piece.domain.toAssignResponse
import com.lms.core_domain.piece.domain.toCreateResponse
import com.lms.core_domain.piece.domain.toUpdateOrderResponse
import com.lms.core_domain.problem.service.ProblemFinder
import com.lms.core_domain.studentpiece.domain.StudentPiece
import com.lms.core_domain.studentpiece.service.StudentPieceFinder
import com.lms.core_domain.studentpiece.service.StudentPieceSaver
import com.lms.core_domain.user.domain.User
import com.lms.core_domain.user.service.UserFinder
import org.springframework.stereotype.Service

@Service
class PieceService(
    private val problemFinder: ProblemFinder,
    private val pieceSaver: PieceSaver,
    private val pieceFinder: PieceFinder,
    private val userFinder: UserFinder,
    private val studentPieceFinder: StudentPieceFinder,
    private val studentPieceSaver: StudentPieceSaver
) {
    fun create(request: PieceCreateRequest): PieceCreateResponse {
        val problems = problemFinder.getProblemsForPiece(problemIds = request.problemIds)
        val piece = Piece(
            problems = problems,
            name = request.name,
            teacherId = request.teacherId,
        )
        val savedPiece = pieceSaver.savePiece(piece)
        return savedPiece.toCreateResponse()
    }

    fun updateProblemOrder(pieceId: Piece.PieceId, request: ProblemOrderUpdateRequest): ProblemOrderResponse {
        val piece = pieceFinder.getWithId(pieceId)
        val reorderedPiece = piece.reorderProblem(request.problemId, request.targetPosition)
        val updatedPiece = pieceSaver.savePiece(reorderedPiece)
        return updatedPiece.toUpdateOrderResponse()
    }

    fun assignToStudents(pieceId: Piece.PieceId, request: PieceAssignRequest): PieceAssignResponse {
        val piece = pieceFinder.getWithId(pieceId)

        val studentUserIds = request.studentIds.map { User.UserId(it) }
        userFinder.validateStudentsExist(studentUserIds)

        val alreadyAssignedStudentIds = studentPieceFinder.findAssignedStudentIds(studentUserIds, pieceId)

        val assignmentResult = piece.assignToStudents(
            requestTeacherId = request.teacherId,
            studentIds = studentUserIds,
            alreadyAssignedStudentIds = alreadyAssignedStudentIds
        )

        if (assignmentResult.hasNewAssignments()) {
            val newStudentPieces = assignmentResult.newAssignments.map { studentId ->
                StudentPiece.assign(studentId, pieceId)
            }
            studentPieceSaver.saveAll(newStudentPieces)
        }

        return assignmentResult.toAssignResponse(piece.getName())
    }
}
