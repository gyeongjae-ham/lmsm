package com.lms.api.piece

import com.lms.core_common.response.ApiResponse
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.request.PieceAssignRequest
import com.lms.core_domain.piece.domain.request.PieceCreateRequest
import com.lms.core_domain.piece.domain.request.PieceScoreRequest
import com.lms.core_domain.piece.domain.request.ProblemOrderUpdateRequest
import com.lms.core_domain.piece.domain.response.PieceAssignResponse
import com.lms.core_domain.piece.domain.response.PieceCreateResponse
import com.lms.core_domain.piece.domain.response.PieceProblemsResponse
import com.lms.core_domain.piece.domain.response.PieceScoreResponse
import com.lms.core_domain.piece.domain.response.ProblemOrderResponse
import com.lms.core_domain.piece.service.PieceService
import com.lms.core_domain.user.domain.User
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path}/pieces")
class PieceController(
    private val pieceService: PieceService
) {
    @PostMapping
    fun create(@RequestBody @Valid request: PieceCreateRequest): ApiResponse<PieceCreateResponse> {
        val pieceResponse = pieceService.create(request)
        return ApiResponse.success(data = pieceResponse)
    }

    @PatchMapping("/{pieceId}/order")
    fun updateProblemOrder(
        @PathVariable pieceId: Long,
        @RequestBody @Valid request: ProblemOrderUpdateRequest
    ): ApiResponse<ProblemOrderResponse> {
        val updatedPiece = pieceService.updateProblemOrder(
            pieceId = Piece.PieceId(pieceId),
            request = request
        )
        return ApiResponse.success(data = updatedPiece)
    }

    @PostMapping("/{pieceId}/assign")
    fun assignToStudents(
        @PathVariable pieceId: Long,
        @ModelAttribute @Valid request: PieceAssignRequest
    ): ApiResponse<PieceAssignResponse> {
        val response = pieceService.assignToStudents(
            pieceId = Piece.PieceId(pieceId),
            request = request
        )
        return ApiResponse.success(data = response)
    }

    @GetMapping("/{pieceId}/problems")
    fun getProblems(
        @PathVariable pieceId: Long,
        @RequestParam studentId: Long
    ): ApiResponse<PieceProblemsResponse> {
        val response = pieceService.getProblemsForStudent(
            pieceId = Piece.PieceId(pieceId),
            studentId = User.UserId(studentId)
        )
        return ApiResponse.success(data = response)
    }

    @PutMapping("/{pieceId}/score")
    fun scoreAnswers(
        @PathVariable pieceId: Long,
        @RequestBody @Valid request: PieceScoreRequest
    ): ApiResponse<PieceScoreResponse> {
        val response = pieceService.scoreAnswers(
            pieceId = Piece.PieceId(pieceId),
            request = request
        )
        return ApiResponse.success(data = response)
    }
}
