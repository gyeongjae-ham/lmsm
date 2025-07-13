package com.lms.api.problem

import com.lms.core.domain.problem.domain.problem.request.ProblemGetRequest
import com.lms.core.domain.problem.domain.problem.response.ProblemFilterResponse
import com.lms.core.domain.problem.service.ProblemGetService
import com.lms.core.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path}/problems")
class ProblemController(
    private val problemGetService: ProblemGetService
) {
    @GetMapping
    fun getProblems(
        @Valid @ModelAttribute request: ProblemGetRequest
    ): ApiResponse<List<ProblemFilterResponse>> {
        val problemFilterResponses = problemGetService.getProblemsWithCondition(request)
        return ApiResponse.success(data = problemFilterResponses)
    }
}

