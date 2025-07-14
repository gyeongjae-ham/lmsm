package com.lms.core_domain.piece.domain.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class PieceAssignRequest(
    @field:NotNull(message = "Teacher ID is required")
    val teacherId: Long,

    @field:NotEmpty(message = "Student IDs cannot be empty")
    val studentIds: List<Long>
)