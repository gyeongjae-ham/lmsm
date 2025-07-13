package com.lms.api.problem

import com.lms.core_domain.enum.ProblemType
import com.lms.core_domain.problem.domain.exception.InvalidValueException
import com.lms.core_domain.problem.domain.response.ProblemFilterResponse
import com.lms.core_domain.problem.service.ProblemGetService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProblemControllerTest : BaseControllerTest() {

    @MockK
    private lateinit var problemGetService: ProblemGetService

    private lateinit var problemController: ProblemController

    override fun getControllers(): Array<Any> {
        problemController = ProblemController(problemGetService)
        return arrayOf(problemController)
    }

    @Test
    fun `GET problems - 정상적인 요청으로 문제 목록을 조회한다`() {
        val mockResponse = listOf(
            ProblemFilterResponse(
                id = 1L,
                unitCode = "uc1580",
                level = 1,
                problemType = ProblemType.SELECTION,
                answer = "1"
            ),
            ProblemFilterResponse(
                id = 2L,
                unitCode = "uc1580",
                level = 2,
                problemType = ProblemType.SUBJECTIVE,
                answer = "2"
            )
        )

        every { problemGetService.getProblemsWithCondition(any()) } returns mockResponse

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "LOW")
                .param("totalCount", "10")
                .param("unitCodeList", "uc1580,uc1581")
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].unitCode").value("uc1580"))
            .andExpect(jsonPath("$.data[0].level").value(1))
            .andExpect(jsonPath("$.data[0].problemType").value("SELECTION"))
            .andExpect(jsonPath("$.data[0].answer").value("1"))

        verify { problemGetService.getProblemsWithCondition(any()) }
    }

    @Test
    fun `GET problems - SELECTION 타입으로 문제를 조회한다`() {
        val mockResponse = listOf(
            ProblemFilterResponse(
                id = 1L,
                unitCode = "uc1580",
                level = 1,
                problemType = ProblemType.SELECTION,
                answer = "1"
            )
        )

        every { problemGetService.getProblemsWithCondition(any()) } returns mockResponse

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "MEDIUM")
                .param("totalCount", "5")
                .param("unitCodeList", "uc1580")
                .param("problemType", "SELECTION")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].problemType").value("SELECTION"))

        verify { problemGetService.getProblemsWithCondition(any()) }
    }

    @Test
    fun `GET problems - SUBJECTIVE 타입으로 문제를 조회한다`() {
        val mockResponse = listOf(
            ProblemFilterResponse(
                id = 1L,
                unitCode = "uc1580",
                level = 1,
                problemType = ProblemType.SUBJECTIVE,
                answer = "Sample answer"
            )
        )

        every { problemGetService.getProblemsWithCondition(any()) } returns mockResponse

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "HIGH")
                .param("totalCount", "3")
                .param("unitCodeList", "uc1580,uc1581")
                .param("problemType", "SUBJECTIVE")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].problemType").value("SUBJECTIVE"))

        verify { problemGetService.getProblemsWithCondition(any()) }
    }

    @Test
    fun `GET problems - 여러 unitCode로 문제를 조회한다`() {
        val mockResponse = listOf(
            ProblemFilterResponse(
                id = 1L,
                unitCode = "uc1580",
                level = 1,
                problemType = ProblemType.SELECTION,
                answer = "1"
            ),
            ProblemFilterResponse(
                id = 2L,
                unitCode = "uc1581",
                level = 2,
                problemType = ProblemType.SELECTION,
                answer = "2"
            ),
            ProblemFilterResponse(
                id = 3L,
                unitCode = "uc1582",
                level = 5,
                problemType = ProblemType.SELECTION,
                answer = "3"
            )
        )

        every { problemGetService.getProblemsWithCondition(any()) } returns mockResponse

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "MEDIUM")
                .param("totalCount", "6")
                .param("unitCodeList", "uc1580,uc1581,uc1582")
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(3))

        verify { problemGetService.getProblemsWithCondition(any()) }
    }

    @Test
    fun `GET problems - 빈 결과를 반환한다`() {
        val mockResponse = emptyList<ProblemFilterResponse>()

        every { problemGetService.getProblemsWithCondition(any()) } returns mockResponse

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "LOW")
                .param("totalCount", "5")
                .param("unitCodeList", "uc9999")
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(0))

        verify { problemGetService.getProblemsWithCondition(any()) }
    }

    @Test
    fun `GET problems - 필수 파라미터 누락 시 400 에러를 반환한다`() {
        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "LOW")
                .param("totalCount", "5")
                // unitCodeList 누락
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET problems - 잘못된 level 값으로 요청 시 400 에러를 반환한다`() {
        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "INVALID_LEVEL")
                .param("totalCount", "5")
                .param("unitCodeList", "uc1580")
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET problems - 잘못된 problemType 값으로 요청 시 400 에러를 반환한다`() {
        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "LOW")
                .param("totalCount", "5")
                .param("unitCodeList", "uc1580")
                .param("problemType", "INVALID_TYPE")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET problems - 음수 totalCount로 요청 시 400 에러를 반환한다`() {
        every { problemGetService.getProblemsWithCondition(any()) } throws InvalidValueException()

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "LOW")
                .param("totalCount", "-1")
                .param("unitCodeList", "uc1580")
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET problems - 0 totalCount로 요청 시 400 에러를 반환한다`() {
        every { problemGetService.getProblemsWithCondition(any()) } throws InvalidValueException()

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "LOW")
                .param("totalCount", "0")
                .param("unitCodeList", "uc1580")
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `컨트롤러 메서드가 올바른 타입을 반환한다`() {
        val mockResponse = listOf(
            ProblemFilterResponse(
                id = 1L,
                unitCode = "uc1580",
                level = 1,
                problemType = ProblemType.SELECTION,
                answer = "1"
            )
        )

        every { problemGetService.getProblemsWithCondition(any()) } returns mockResponse

        mockMvc.perform(
            get("/api/v1/problems")
                .param("level", "LOW")
                .param("totalCount", "5")
                .param("unitCodeList", "uc1580")
                .param("problemType", "ALL")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].unitCode").value("uc1580"))
            .andExpect(jsonPath("$.data[0].level").value(1))
            .andExpect(jsonPath("$.data[0].problemType").value("SELECTION"))
            .andExpect(jsonPath("$.data[0].answer").value("1"))

        verify { problemGetService.getProblemsWithCondition(any()) }
    }
}
