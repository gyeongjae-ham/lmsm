package com.lms.core_domain.piece.domain.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class PieceScoreRequest(
    @field:NotNull(message = "Student ID is required")
    @field:Positive(message = "Student ID must be positive")
    val studentId: Long,

    @field:NotEmpty(message = "Answers cannot be empty")
    @field:Valid
    val answers: List<StudentAnswerRequest>
)

data class StudentAnswerRequest(
    @field:NotNull(message = "Problem ID is required")
    @field:Positive(message = "Problem ID must be positive")
    val problemId: Long,

    @field:NotNull(message = "Student answer is required")
    val studentAnswer: String
)