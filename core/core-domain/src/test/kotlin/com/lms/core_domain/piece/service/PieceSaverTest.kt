package com.lms.core_domain.piece.service

import com.lms.core_common.enum.ProblemType
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.repository.PieceRepository
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PieceSaverTest {

    @MockK
    private lateinit var pieceRepository: PieceRepository

    @InjectMockKs
    private lateinit var pieceSaver: PieceSaver

    @Test
    fun `Piece를 저장하고 저장된 Piece를 반환한다`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1L),
                unitCode = "uc1580",
                level = 1,
                problemType = ProblemType.SELECTION,
                answer = "1"
            )
        )
        val piece = Piece(
            name = "테스트 학습지",
            teacherId = 1L,
            problems = Problems(problemList)
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(1L),
            name = "테스트 학습지",
            teacherId = 1L,
            problems = Problems(problemList)
        )

        every { pieceRepository.savePiece(piece) } returns savedPiece

        val result = pieceSaver.savePiece(piece)

        assertThat(result).isEqualTo(savedPiece)
        assertThat(result.getName()).isEqualTo("테스트 학습지")
        assertThat(result.getTeacherId()).isEqualTo(1L)
        verify { pieceRepository.savePiece(piece) }
    }

    @Test
    fun `여러 개의 Problem을 가진 Piece를 저장한다`() {
        val problemList = listOf(
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
                level = 3,
                problemType = ProblemType.SELECTION,
                answer = "3"
            )
        )
        val piece = Piece(
            name = "다중 문제 학습지",
            teacherId = 2L,
            problems = Problems(problemList)
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(2L),
            name = "다중 문제 학습지",
            teacherId = 2L,
            problems = Problems(problemList)
        )

        every { pieceRepository.savePiece(piece) } returns savedPiece

        val result = pieceSaver.savePiece(piece)

        assertThat(result).isEqualTo(savedPiece)
        assertThat(result.getProblemCount()).isEqualTo(3)
        verify { pieceRepository.savePiece(piece) }
    }

    @Test
    fun `Repository의 savePiece 메소드가 정확히 한 번 호출된다`() {
        val problemList = listOf(
            Problem(
                problemId = Problem.ProblemId(1L),
                unitCode = "uc1580",
                level = 1,
                problemType = ProblemType.SELECTION,
                answer = "1"
            )
        )
        val piece = Piece(
            name = "검증 테스트 학습지",
            teacherId = 3L,
            problems = Problems(problemList)
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(3L),
            name = "검증 테스트 학습지",
            teacherId = 3L,
            problems = Problems(problemList)
        )

        every { pieceRepository.savePiece(piece) } returns savedPiece

        pieceSaver.savePiece(piece)

        verify(exactly = 1) { pieceRepository.savePiece(piece) }
    }
}
