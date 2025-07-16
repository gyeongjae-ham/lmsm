package com.lms.core_domain.piece.domain.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class PieceCreateRequest(
    @field:NotBlank(message = "Piece name is required")
    val name: String,

    @field:NotNull(message = "Teacher ID is required")
    @field:Positive(message = "Teacher ID must be positive")
    val teacherId: Long,

    @field:NotEmpty(message = "Problem list is required")
    val problemIds: List<Long>
)
