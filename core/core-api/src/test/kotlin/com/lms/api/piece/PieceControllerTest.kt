package com.lms.api.piece

import com.lms.api.problem.BaseControllerTest
import com.lms.core_domain.piece.domain.response.PieceAssignResponse
import com.lms.core_domain.piece.service.PieceService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PieceControllerTest : BaseControllerTest() {

    @MockK
    private lateinit var pieceService: PieceService
    private lateinit var pieceController: PieceController

    override fun getControllers(): Array<Any> {
        pieceController = PieceController(pieceService)
        return arrayOf(pieceController)
    }

    @Test
    fun `학습지 출제 API가 정상적으로 처리된다`() {
        val pieceId = 1L
        val teacherId = 1L
        val studentIds = listOf(2L, 3L)

        val response = PieceAssignResponse(
            pieceId = pieceId,
            pieceName = "테스트 학습지",
            assignedStudentCount = 2,
            skippedStudentCount = 0
        )

        every { pieceService.assignToStudents(any(), any()) } returns response

        mockMvc.perform(
            post("/api/v1/pieces/{pieceId}/assign", pieceId)
                .param("teacherId", teacherId.toString())
                .param("studentIds", studentIds[0].toString())
                .param("studentIds", studentIds[1].toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.pieceId").value(pieceId))
            .andExpect(jsonPath("$.data.pieceName").value("테스트 학습지"))
            .andExpect(jsonPath("$.data.assignedStudentCount").value(2))
            .andExpect(jsonPath("$.data.skippedStudentCount").value(0))
    }

    @Test
    fun `부분 출제 시 정상적으로 응답된다`() {
        val pieceId = 1L
        val teacherId = 1L
        val studentIds = listOf(2L, 3L, 4L)

        val response = PieceAssignResponse(
            pieceId = pieceId,
            pieceName = "테스트 학습지",
            assignedStudentCount = 2,
            skippedStudentCount = 1
        )

        every { pieceService.assignToStudents(any(), any()) } returns response

        mockMvc.perform(
            post("/api/v1/pieces/{pieceId}/assign", pieceId)
                .param("teacherId", teacherId.toString())
                .param("studentIds", studentIds[0].toString())
                .param("studentIds", studentIds[1].toString())
                .param("studentIds", studentIds[2].toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.assignedStudentCount").value(2))
            .andExpect(jsonPath("$.data.skippedStudentCount").value(1))
    }

    @Test
    fun `필수 파라미터가 누락되면 400 에러가 발생한다`() {
        val pieceId = 1L

        mockMvc.perform(
            post("/api/v1/pieces/{pieceId}/assign", pieceId)
                .param("teacherId", "1")
            // studentIds 누락
        )
            .andExpect(status().isBadRequest)
    }
}
