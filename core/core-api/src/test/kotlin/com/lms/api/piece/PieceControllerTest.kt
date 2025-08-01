package com.lms.api.piece

import com.lms.api.problem.BaseControllerTest
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.response.PieceAssignResponse
import com.lms.core_domain.piece.domain.response.PieceProblemsResponse
import com.lms.core_domain.piece.domain.response.PieceScoreResponse
import com.lms.core_domain.piece.domain.response.ProblemResponse
import com.lms.core_domain.piece.domain.response.ScoreResultResponse
import com.lms.core_domain.piece.service.PieceService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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

    @Test
    fun `학습지 문제 조회 API가 정상적으로 처리된다`() {
        val pieceId = 1L
        val userId = 2L

        val response = PieceProblemsResponse(
            pieceId = pieceId,
            pieceName = "테스트 학습지",
            teacherId = 1L,
            problemCount = 2,
            problems = listOf(
                ProblemResponse(
                    problemId = 1L,
                    unitCode = "uc1580",
                    level = 1,
                    problemType = "SELECTION",
                    sequence = 10
                ),
                ProblemResponse(
                    problemId = 2L,
                    unitCode = "uc1580",
                    level = 2,
                    problemType = "SUBJECTIVE",
                    sequence = 20
                )
            )
        )

        every { pieceService.getProblemsForStudent(any(), any()) } returns response

        mockMvc.perform(
            get("/api/v1/pieces/{pieceId}/problems", pieceId)
                .param("studentId", userId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.pieceId").value(pieceId))
            .andExpect(jsonPath("$.data.pieceName").value("테스트 학습지"))
            .andExpect(jsonPath("$.data.teacherId").value(1L))
            .andExpect(jsonPath("$.data.problemCount").value(2))
            .andExpect(jsonPath("$.data.problems").isArray)
            .andExpect(jsonPath("$.data.problems[0].problemId").value(1L))
            .andExpect(jsonPath("$.data.problems[0].unitCode").value("uc1580"))
            .andExpect(jsonPath("$.data.problems[0].level").value(1))
            .andExpect(jsonPath("$.data.problems[0].problemType").value("SELECTION"))
            .andExpect(jsonPath("$.data.problems[0].sequence").value(10))
            .andExpect(jsonPath("$.data.problems[1].problemId").value(2L))
            .andExpect(jsonPath("$.data.problems[1].level").value(2))
            .andExpect(jsonPath("$.data.problems[1].problemType").value("SUBJECTIVE"))
    }

    @Test
    fun `userId 파라미터가 누락되면 400 에러가 발생한다`() {
        val pieceId = 1L

        mockMvc.perform(
            get("/api/v1/pieces/{pieceId}/problems", pieceId)
            // userId 파라미터 누락
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `학습지 채점 API가 정상적으로 처리된다`() {
        val pieceId = 1L
        val studentId = 2L

        val response = PieceScoreResponse(
            pieceId = pieceId,
            studentId = studentId,
            totalProblems = 2,
            correctCount = 1,
            scoreRate = 50.0,
            results = listOf(
                ScoreResultResponse(
                    problemId = 1L,
                    studentAnswer = "정답1",
                    correctAnswer = "정답1",
                    isCorrect = true
                ),
                ScoreResultResponse(
                    problemId = 2L,
                    studentAnswer = "오답2",
                    correctAnswer = "정답2",
                    isCorrect = false
                )
            )
        )

        every { pieceService.scoreAnswers(any(), any()) } returns response

        val requestBody = """
            {
                "studentId": $studentId,
                "answers": [
                    {
                        "problemId": 1,
                        "studentAnswer": "정답1"
                    },
                    {
                        "problemId": 2,
                        "studentAnswer": "오답2"
                    }
                ]
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/pieces/{pieceId}/score", pieceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.pieceId").value(pieceId))
            .andExpect(jsonPath("$.data.studentId").value(studentId))
            .andExpect(jsonPath("$.data.totalProblems").value(2))
            .andExpect(jsonPath("$.data.correctCount").value(1))
            .andExpect(jsonPath("$.data.scoreRate").value(50.0))
            .andExpect(jsonPath("$.data.results").isArray)
            .andExpect(jsonPath("$.data.results[0].problemId").value(1L))
            .andExpect(jsonPath("$.data.results[0].isCorrect").value(true))
            .andExpect(jsonPath("$.data.results[1].problemId").value(2L))
            .andExpect(jsonPath("$.data.results[1].isCorrect").value(false))
    }

    @Test
    fun `채점 요청에서 필수 필드가 누락되면 400 에러가 발생한다`() {
        val pieceId = 1L
        val requestBody = """
            {
                "studentId": 2,
                "answers": []
            }
        """.trimIndent()

        mockMvc.perform(
            put("/api/v1/pieces/{pieceId}/score", pieceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `이미 답변을 제출한 학습지는 재채점할 수 없다`() {
        val pieceId = 1L
        val studentId = 2L
        val requestBody = """
            {
                "studentId": $studentId,
                "answers": [
                    {
                        "problemId": 1,
                        "studentAnswer": "정답1"
                    }
                ]
            }
        """.trimIndent()

        every { pieceService.scoreAnswers(any(), any()) } throws BusinessException("Answers have already been submitted for this piece")

        mockMvc.perform(
            put("/api/v1/pieces/{pieceId}/score", pieceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `학습지 통계 분석 API가 정상적으로 처리된다`() {
        val pieceId = 1L
        val teacherId = 1L

        val response = com.lms.core_domain.piece.domain.response.PieceAnalyzeResponse(
            pieceId = pieceId,
            pieceName = "테스트 학습지",
            teacherId = teacherId,
            totalAssignedStudents = 2,
            submittedStudents = 2,
            submissionRate = 100.0,
            studentStatistics = listOf(
                com.lms.core_domain.piece.domain.response.StudentStatistic(
                    studentId = 2L,
                    totalProblems = 2,
                    correctCount = 2,
                    scoreRate = 100.0,
                    hasSubmitted = true
                ),
                com.lms.core_domain.piece.domain.response.StudentStatistic(
                    studentId = 3L,
                    totalProblems = 2,
                    correctCount = 1,
                    scoreRate = 50.0,
                    hasSubmitted = true
                )
            ),
            problemStatistics = listOf(
                com.lms.core_domain.piece.domain.response.ProblemStatistic(
                    problemId = 1L,
                    unitCode = "uc1580",
                    level = 1,
                    problemType = "SELECTION",
                    totalSubmissions = 2,
                    correctSubmissions = 2,
                    correctRate = 100.0
                ),
                com.lms.core_domain.piece.domain.response.ProblemStatistic(
                    problemId = 2L,
                    unitCode = "uc1580",
                    level = 2,
                    problemType = "SUBJECTIVE",
                    totalSubmissions = 2,
                    correctSubmissions = 1,
                    correctRate = 50.0
                )
            )
        )

        every { pieceService.analyzePiece(any(), any()) } returns response

        mockMvc.perform(
            get("/api/v1/pieces/{pieceId}/analyze", pieceId)
                .param("teacherId", teacherId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.pieceId").value(pieceId))
            .andExpect(jsonPath("$.data.pieceName").value("테스트 학습지"))
            .andExpect(jsonPath("$.data.teacherId").value(teacherId))
            .andExpect(jsonPath("$.data.totalAssignedStudents").value(2))
            .andExpect(jsonPath("$.data.submittedStudents").value(2))
            .andExpect(jsonPath("$.data.submissionRate").value(100.0))
            .andExpect(jsonPath("$.data.studentStatistics").isArray)
            .andExpect(jsonPath("$.data.studentStatistics").value(org.hamcrest.Matchers.hasSize<Any>(2)))
            .andExpect(jsonPath("$.data.problemStatistics").isArray)
            .andExpect(jsonPath("$.data.problemStatistics").value(org.hamcrest.Matchers.hasSize<Any>(2)))
            .andExpect(jsonPath("$.data.studentStatistics[0].studentId").value(2L))
            .andExpect(jsonPath("$.data.studentStatistics[0].scoreRate").value(100.0))
            .andExpect(jsonPath("$.data.studentStatistics[1].studentId").value(3L))
            .andExpect(jsonPath("$.data.studentStatistics[1].scoreRate").value(50.0))
            .andExpect(jsonPath("$.data.problemStatistics[0].problemId").value(1L))
            .andExpect(jsonPath("$.data.problemStatistics[0].correctRate").value(100.0))
            .andExpect(jsonPath("$.data.problemStatistics[1].problemId").value(2L))
            .andExpect(jsonPath("$.data.problemStatistics[1].correctRate").value(50.0))
    }

    @Test
    fun `권한이 없는 선생님이 학습지 통계를 조회하려고 하면 400 에러가 발생한다`() {
        val pieceId = 1L
        val teacherId = 2L

        every { pieceService.analyzePiece(any(), any()) } throws BusinessException("학습지에 대한 권한이 없습니다.")

        mockMvc.perform(
            get("/api/v1/pieces/{pieceId}/analyze", pieceId)
                .param("teacherId", teacherId.toString())
        )
            .andExpect(status().isBadRequest)
    }
}
