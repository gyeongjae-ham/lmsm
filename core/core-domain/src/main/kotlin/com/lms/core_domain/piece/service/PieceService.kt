package com.lms.core_domain.piece.service

import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.PieceStatistics
import com.lms.core_domain.piece.domain.request.PieceAssignRequest
import com.lms.core_domain.piece.domain.request.PieceCreateRequest
import com.lms.core_domain.piece.domain.request.PieceScoreRequest
import com.lms.core_domain.piece.domain.request.ProblemOrderUpdateRequest
import com.lms.core_domain.piece.domain.response.PieceAnalyzeResponse
import com.lms.core_domain.piece.domain.response.PieceAssignResponse
import com.lms.core_domain.piece.domain.response.PieceCreateResponse
import com.lms.core_domain.piece.domain.response.PieceProblemsResponse
import com.lms.core_domain.piece.domain.response.PieceScoreResponse
import com.lms.core_domain.piece.domain.response.ProblemOrderResponse
import com.lms.core_domain.piece.domain.response.ProblemResponse
import com.lms.core_domain.piece.domain.toAssignResponse
import com.lms.core_domain.piece.domain.toCreateResponse
import com.lms.core_domain.piece.domain.toUpdateOrderResponse
import com.lms.core_domain.problem.service.ProblemFinder
import com.lms.core_domain.studentanswer.service.StudentAnswerFinder
import com.lms.core_domain.studentanswer.service.StudentAnswerSaver
import com.lms.core_domain.studentanswer.service.StudentAnswerScorer
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
    private val studentPieceSaver: StudentPieceSaver,
    private val studentAnswerScorer: StudentAnswerScorer,
    private val studentAnswerSaver: StudentAnswerSaver,
    private val studentAnswerFinder: StudentAnswerFinder
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

    // 이미 배정된 학생은 제외하고 새로운 학생들만 배정하는 중복 배정 방지 로직
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

    fun getProblemsForStudent(pieceId: Piece.PieceId, studentId: User.UserId): PieceProblemsResponse {
        studentPieceFinder.validateStudentHasPiece(studentId, pieceId)

        val piece = pieceFinder.getWithId(pieceId)

        return PieceProblemsResponse(
            pieceId = piece.id.value,
            pieceName = piece.getName(),
            teacherId = piece.getTeacherId(),
            problemCount = piece.getProblemCount(),
            problems = piece.getProblemsWithSequence()
                .sortedBy { it.sequence }
                .map { problemWithSequence ->
                    ProblemResponse(
                        problemId = problemWithSequence.problem.id.value,
                        unitCode = problemWithSequence.problem.unitCode,
                        level = problemWithSequence.problem.level,
                        problemType = problemWithSequence.problem.problemType.name,
                        sequence = problemWithSequence.sequence
                    )
                }
        )
    }

    // 병렬 처리를 통한 채점 및 중복 제출 방지 로직
    fun scoreAnswers(pieceId: Piece.PieceId, request: PieceScoreRequest): PieceScoreResponse {
        val studentId = User.UserId(request.studentId)

        studentPieceFinder.validateStudentHasPiece(studentId = studentId, pieceId = pieceId)

        if (studentAnswerFinder.hasSubmittedAnswers(studentId, pieceId)) {
            throw BusinessException("Answers have already been submitted for this piece")
        }

        val piece = pieceFinder.getWithId(pieceId)

        val scoreResults =
            studentAnswerScorer.scoreAnswers(
                studentId = studentId,
                pieceId = pieceId,
                piece = piece,
                request = request
            )

        val studentAnswers = scoreResults.map { it.second }
        studentAnswerSaver.saveAll(studentAnswers)

        val results = scoreResults.map { it.first }
        val correctCount = results.count { it.isCorrect }
        val scoreRate = if (results.isNotEmpty()) {
            (correctCount.toDouble() / results.size * 100)
        } else {
            0.0
        }

        return PieceScoreResponse(
            pieceId = pieceId.value,
            studentId = studentId.value,
            totalProblems = results.size,
            correctCount = correctCount,
            scoreRate = scoreRate,
            results = results
        )
    }

    // 선생님 권한 검증 및 통계 계산 로직
    fun analyzePiece(pieceId: Piece.PieceId, teacherId: Long): PieceAnalyzeResponse {
        val piece = pieceFinder.getWithId(pieceId)
        if (piece.getTeacherId() != teacherId) {
            throw BusinessException("You do not have permission to access this piece")
        }

        val assignedStudents = studentPieceFinder.findAssignedStudents(pieceId)
        val studentAnswers = studentAnswerFinder.findByPieceId(pieceId)

        val statistics = PieceStatistics(piece, assignedStudents, studentAnswers)
        val studentStats = statistics.calculateStudentStatistics()
        val problemStats = statistics.calculateProblemStatistics()
        val overallStats = statistics.calculateOverallStatistics()

        return PieceAnalyzeResponse(
            pieceId = piece.id.value,
            pieceName = piece.getName(),
            teacherId = piece.getTeacherId(),
            totalAssignedStudents = overallStats.totalAssignedStudents,
            submittedStudents = overallStats.submittedStudents,
            submissionRate = overallStats.submissionRate,
            studentStatistics = studentStats,
            problemStatistics = problemStats
        )
    }
}
