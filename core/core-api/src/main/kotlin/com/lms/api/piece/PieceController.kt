package com.lms.api.piece

import com.lms.core_common.response.ApiResponse
import com.lms.core_domain.piece.domain.request.PieceCreateRequest
import com.lms.core_domain.piece.domain.response.PieceCreateResponse
import com.lms.core_domain.piece.service.PieceService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
}
