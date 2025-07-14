package com.lms.core_domain.studentpiece.service

import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentpiece.domain.StudentPiece
import com.lms.core_domain.studentpiece.domain.repository.StudentPieceRepository
import com.lms.core_domain.user.domain.User
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StudentPieceSaverTest {

    @MockK
    private lateinit var studentPieceRepository: StudentPieceRepository

    private lateinit var studentPieceSaver: StudentPieceSaver

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        studentPieceSaver = StudentPieceSaver(studentPieceRepository)
    }

    @Test
    fun `여러 StudentPiece를 정상적으로 저장한다`() {
        val studentPieces = listOf(
            StudentPiece.assign(User.UserId(1L), Piece.PieceId(1L)),
            StudentPiece.assign(User.UserId(2L), Piece.PieceId(1L))
        )
        val savedStudentPieces = listOf(
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(1L),
                studentId = User.UserId(1L),
                pieceId = Piece.PieceId(1L)
            ),
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(2L),
                studentId = User.UserId(2L),
                pieceId = Piece.PieceId(1L)
            )
        )

        every { studentPieceRepository.saveAll(studentPieces) } returns savedStudentPieces

        val result = studentPieceSaver.saveAll(studentPieces)

        assertThat(result).hasSize(2)
        assertThat(result[0].id.value).isEqualTo(1L)
        assertThat(result[0].getStudentId()).isEqualTo(User.UserId(1L))
        assertThat(result[1].id.value).isEqualTo(2L)
        assertThat(result[1].getStudentId()).isEqualTo(User.UserId(2L))
        verify { studentPieceRepository.saveAll(studentPieces) }
    }

    @Test
    fun `빈 StudentPiece 목록을 저장할 때 빈 목록을 반환한다`() {
        val emptyStudentPieces = emptyList<StudentPiece>()

        every { studentPieceRepository.saveAll(emptyStudentPieces) } returns emptyList()

        val result = studentPieceSaver.saveAll(emptyStudentPieces)

        assertThat(result).isEmpty()
        verify { studentPieceRepository.saveAll(emptyStudentPieces) }
    }

    @Test
    fun `단일 StudentPiece를 정상적으로 저장한다`() {
        val studentPiece = StudentPiece.assign(User.UserId(1L), Piece.PieceId(1L))
        val savedStudentPiece = StudentPiece(
            studentPieceId = StudentPiece.StudentPieceId(1L),
            studentId = User.UserId(1L),
            pieceId = Piece.PieceId(1L)
        )

        every { studentPieceRepository.saveAll(listOf(studentPiece)) } returns listOf(savedStudentPiece)

        val result = studentPieceSaver.saveAll(listOf(studentPiece))

        assertThat(result).hasSize(1)
        assertThat(result[0].id.value).isEqualTo(1L)
        assertThat(result[0].getStudentId()).isEqualTo(User.UserId(1L))
        assertThat(result[0].getPieceId()).isEqualTo(Piece.PieceId(1L))
        verify { studentPieceRepository.saveAll(listOf(studentPiece)) }
    }
}
