package com.lms.core_domain.piece.service

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.ProblemWithSequence
import com.lms.core_domain.piece.domain.repository.PieceRepository
import com.lms.core_domain.problem.domain.Problem
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PieceFinderTest {

    @MockK
    private lateinit var pieceRepository: PieceRepository

    private lateinit var pieceFinder: PieceFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        pieceFinder = PieceFinder(pieceRepository)
    }

    @Test
    fun `존재하는 Piece ID로 조회 시 정상적으로 반환된다`() {
        val pieceId = Piece.PieceId(1L)
        val problemsWithSequence = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                ),
                sequence = 20
            )
        )
        val expectedPiece = Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = problemsWithSequence
        )

        every { pieceRepository.findById(pieceId) } returns expectedPiece

        val result = pieceFinder.getWithId(pieceId)

        assertThat(result).isEqualTo(expectedPiece)
        assertThat(result.id).isEqualTo(pieceId)
        assertThat(result.getName()).isEqualTo("테스트 학습지")
        assertThat(result.getTeacherId()).isEqualTo(1L)
        assertThat(result.getProblemCount()).isEqualTo(2)
        verify { pieceRepository.findById(pieceId) }
    }

    @Test
    fun `존재하지 않는 Piece ID로 조회 시 BusinessException이 발생한다`() {
        val nonExistentPieceId = Piece.PieceId(999L)

        every { pieceRepository.findById(nonExistentPieceId) } throws BusinessException("Piece not found")

        assertThatThrownBy { pieceFinder.getWithId(nonExistentPieceId) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Piece not found")

        verify { pieceRepository.findById(nonExistentPieceId) }
    }

    @Test
    fun `단일 문제를 가진 Piece 조회가 정상적으로 처리된다`() {
        val pieceId = Piece.PieceId(2L)
        val problemsWithSequence = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            )
        )
        val expectedPiece = Piece(
            pieceId = pieceId,
            name = "단일 문제 학습지",
            teacherId = 2L,
            problemsWithSequence = problemsWithSequence
        )

        every { pieceRepository.findById(pieceId) } returns expectedPiece

        val result = pieceFinder.getWithId(pieceId)

        assertThat(result).isEqualTo(expectedPiece)
        assertThat(result.getProblemCount()).isEqualTo(1)
        assertThat(result.getProblemsWithSequence()).hasSize(1)
        assertThat(result.getProblemsWithSequence()[0].sequence).isEqualTo(10)
        verify { pieceRepository.findById(pieceId) }
    }

    @Test
    fun `여러 문제를 가진 Piece 조회가 정상적으로 처리된다`() {
        val pieceId = Piece.PieceId(3L)
        val problemsWithSequence = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                ),
                sequence = 20
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1583",
                    level = 3,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                ),
                sequence = 30
            )
        )
        val expectedPiece = Piece(
            pieceId = pieceId,
            name = "다중 문제 학습지",
            teacherId = 3L,
            problemsWithSequence = problemsWithSequence
        )

        every { pieceRepository.findById(pieceId) } returns expectedPiece

        val result = pieceFinder.getWithId(pieceId)

        assertThat(result).isEqualTo(expectedPiece)
        assertThat(result.getProblemCount()).isEqualTo(3)
        assertThat(result.getProblemsWithSequence()).hasSize(3)
        verify { pieceRepository.findById(pieceId) }
    }

    @Test
    fun `Repository 호출이 정확히 한 번만 발생한다`() {
        val pieceId = Piece.PieceId(4L)
        val problemsWithSequence = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            )
        )
        val expectedPiece = Piece(
            pieceId = pieceId,
            name = "호출 검증 학습지",
            teacherId = 4L,
            problemsWithSequence = problemsWithSequence
        )

        every { pieceRepository.findById(pieceId) } returns expectedPiece

        pieceFinder.getWithId(pieceId)

        verify(exactly = 1) { pieceRepository.findById(pieceId) }
    }

    @Test
    fun `비어있는 문제 목록을 가진 Piece 조회가 정상적으로 처리된다`() {
        val pieceId = Piece.PieceId(5L)
        val expectedPiece = Piece(
            pieceId = pieceId,
            name = "빈 학습지",
            teacherId = 5L,
            problemsWithSequence = emptyList()
        )

        every { pieceRepository.findById(pieceId) } returns expectedPiece

        val result = pieceFinder.getWithId(pieceId)

        assertThat(result).isEqualTo(expectedPiece)
        assertThat(result.getProblemCount()).isEqualTo(0)
        assertThat(result.getProblemsWithSequence()).isEmpty()
        verify { pieceRepository.findById(pieceId) }
    }
}
