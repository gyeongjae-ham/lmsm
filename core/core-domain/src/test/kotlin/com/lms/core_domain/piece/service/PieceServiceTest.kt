package com.lms.core_domain.piece.service

import com.lms.core_common.enum.ProblemType
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.request.PieceCreateRequest
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
import com.lms.core_domain.problem.service.ProblemFinder
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PieceServiceTest {

    @MockK
    private lateinit var problemFinder: ProblemFinder

    @MockK
    private lateinit var pieceSaver: PieceSaver

    private lateinit var pieceService: PieceService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        pieceService = PieceService(problemFinder, pieceSaver)
    }

    @Test
    fun `학습지 생성 요청이 성공적으로 처리된다`() {
        val request = PieceCreateRequest(
            name = "테스트 학습지",
            teacherId = 1L,
            problemIds = listOf(1L, 2L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                )
            )
        )
        val piece = Piece(
            name = "테스트 학습지",
            teacherId = 1L,
            problems = problems
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(1L),
            name = "테스트 학습지",
            teacherId = 1L,
            problems = problems
        )

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        val result = pieceService.create(request)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("테스트 학습지")
        assertThat(result.teacherId).isEqualTo(1L)
        assertThat(result.problemCount).isEqualTo(2)
        verify { problemFinder.getProblemsForPiece(request.problemIds) }
        verify { pieceSaver.savePiece(any()) }
    }

    @Test
    fun `단일 문제로 학습지를 생성한다`() {
        val request = PieceCreateRequest(
            name = "단일 문제 학습지",
            teacherId = 2L,
            problemIds = listOf(1L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                )
            )
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(2L),
            name = "단일 문제 학습지",
            teacherId = 2L,
            problems = problems
        )
        pieceService = PieceService(problemFinder, pieceSaver)

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        val result = pieceService.create(request)

        assertThat(result.id).isEqualTo(2L)
        assertThat(result.name).isEqualTo("단일 문제 학습지")
        assertThat(result.teacherId).isEqualTo(2L)
        assertThat(result.problemCount).isEqualTo(1)
    }

    @Test
    fun `여러 문제로 학습지를 생성한다`() {
        val request = PieceCreateRequest(
            name = "다중 문제 학습지",
            teacherId = 3L,
            problemIds = listOf(1L, 2L, 3L, 4L, 5L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                ),
                Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1583",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                ),
                Problem(
                    problemId = Problem.ProblemId(4L),
                    unitCode = "uc1583",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "4"
                ),
                Problem(
                    problemId = Problem.ProblemId(5L),
                    unitCode = "uc1585",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "5"
                )
            )
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(3L),
            name = "다중 문제 학습지",
            teacherId = 3L,
            problems = problems
        )
        pieceService = PieceService(problemFinder, pieceSaver)

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        val result = pieceService.create(request)

        assertThat(result.id).isEqualTo(3L)
        assertThat(result.name).isEqualTo("다중 문제 학습지")
        assertThat(result.teacherId).isEqualTo(3L)
        assertThat(result.problemCount).isEqualTo(5)
    }

    @Test
    fun `서비스 호출 순서가 올바르다`() {
        val request = PieceCreateRequest(
            name = "순서 검증 학습지",
            teacherId = 4L,
            problemIds = listOf(1L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                )
            )
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(4L),
            name = "순서 검증 학습지",
            teacherId = 4L,
            problems = problems
        )
        pieceService = PieceService(problemFinder, pieceSaver)

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        pieceService.create(request)

        verify(exactly = 1) { problemFinder.getProblemsForPiece(request.problemIds) }
        verify(exactly = 1) { pieceSaver.savePiece(any()) }
    }
}
