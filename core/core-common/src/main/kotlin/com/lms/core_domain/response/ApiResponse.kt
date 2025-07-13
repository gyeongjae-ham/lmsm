package com.lms.core_domain.response

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: T? = null,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val validationErrors: List<ValidationError> = emptyList()
) {
    companion object {
        fun <T> success(data: T, message: String = "success"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                status = HttpStatus.OK.value(),
                message = message,
                data = data
            )
        }

        fun success(message: String = "success"): ApiResponse<Unit> {
            return ApiResponse(
                success = true,
                status = HttpStatus.OK.value(),
                message = message,
                data = null
            )
        }

        fun <T> error(
            status: HttpStatus,
            message: String,
            validationErrors: List<ValidationError> = emptyList()
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                status = status.value(),
                message = message,
                data = null,
                validationErrors = validationErrors
            )
        }
    }
}

data class ValidationError(
    val field: String,
    val message: String,
    val rejectedValue: String? = null
)
