package com.lms.api.problem

import com.lms.core.domain.problem.domain.problem.request.ProblemGetRequest
import com.lms.core.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path}/problems")
class ProblemController {
    @GetMapping
    fun getProblems(
        @Valid @ModelAttribute request: ProblemGetRequest
    ): ApiResponse<Unit> {
        return ApiResponse.success()
    }
}

