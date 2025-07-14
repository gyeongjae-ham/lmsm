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

class StudentPieceFinderTest {

    @MockK
    private lateinit var studentPieceRepository: StudentPieceRepository

    private lateinit var studentPieceFinder: StudentPieceFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        studentPieceFinder = StudentPieceFinder(studentPieceRepository)
    }

    @Test
    fun `이미 출제된 학생 ID 목록을 정상적으로 반환한다`() {
        val studentIds = listOf(User.UserId(1L), User.UserId(2L), User.UserId(3L))
        val pieceId = Piece.PieceId(1L)
        val assignedStudentPieces = listOf(
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(1L),
                studentId = User.UserId(1L),
                pieceId = pieceId
            ),
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(2L),
                studentId = User.UserId(3L),
                pieceId = pieceId
            )
        )

        every { studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId) } returns assignedStudentPieces

        val result = studentPieceFinder.findAssignedStudentIds(studentIds, pieceId)

        assertThat(result).hasSize(2)
        assertThat(result).containsExactlyInAnyOrder(User.UserId(1L), User.UserId(3L))
        verify { studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId) }
    }

    @Test
    fun `출제된 학생이 없을 때 빈 목록을 반환한다`() {
        val studentIds = listOf(User.UserId(1L), User.UserId(2L))
        val pieceId = Piece.PieceId(1L)

        every { studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId) } returns emptyList()

        val result = studentPieceFinder.findAssignedStudentIds(studentIds, pieceId)

        assertThat(result).isEmpty()
        verify { studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId) }
    }

    @Test
    fun `모든 학생이 이미 출제받은 경우 모든 학생 ID를 반환한다`() {
        val studentIds = listOf(User.UserId(1L), User.UserId(2L))
        val pieceId = Piece.PieceId(1L)
        val allAssignedStudentPieces = listOf(
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(1L),
                studentId = User.UserId(1L),
                pieceId = pieceId
            ),
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(2L),
                studentId = User.UserId(2L),
                pieceId = pieceId
            )
        )

        every { studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId) } returns allAssignedStudentPieces

        val result = studentPieceFinder.findAssignedStudentIds(studentIds, pieceId)

        assertThat(result).hasSize(2)
        assertThat(result).containsExactlyInAnyOrder(User.UserId(1L), User.UserId(2L))
        verify { studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId) }
    }
}
