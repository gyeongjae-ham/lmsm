package com.lms.core_domain.piece.domain.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ProblemOrderUpdateRequest(
    @field:NotNull(message = "Problem ID is required")
    @field:Positive(message = "Problem ID must be positive")
    val problemId: Long,

    @field:NotNull(message = "Target position is required")
    @field:Min(value = 0, message = "Target position must be 0 or greater")
    val targetPosition: Int
)