package com.lms.core_domain.piece.domain

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
import com.lms.core_domain.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PieceAssignToStudentsTest {

    @Test
    fun `정상적으로 학생들에게 학습지를 출제한다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(User.UserId(2L), User.UserId(3L), User.UserId(4L))
        val alreadyAssignedIds = emptyList<User.UserId>()

        val result = piece.assignToStudents(
            requestTeacherId = 1L,
            studentIds = studentIds,
            alreadyAssignedStudentIds = alreadyAssignedIds
        )

        assertThat(result.pieceId.value).isEqualTo(1L)
        assertThat(result.newAssignments).hasSize(3)
        assertThat(result.newAssignments).containsExactlyInAnyOrder(
            User.UserId(2L), User.UserId(3L), User.UserId(4L)
        )
        assertThat(result.skippedStudents).isEmpty()
        assertThat(result.hasNewAssignments()).isTrue()
        assertThat(result.getAssignmentCount()).isEqualTo(3)
        assertThat(result.getSkippedCount()).isEqualTo(0)
    }

    @Test
    fun `이미 출제받은 학생들은 스킵하고 새로운 학생들만 출제한다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(User.UserId(2L), User.UserId(3L), User.UserId(4L))
        val alreadyAssignedIds = listOf(User.UserId(2L), User.UserId(4L))

        val result = piece.assignToStudents(
            requestTeacherId = 1L,
            studentIds = studentIds,
            alreadyAssignedStudentIds = alreadyAssignedIds
        )

        assertThat(result.pieceId.value).isEqualTo(1L)
        assertThat(result.newAssignments).hasSize(1)
        assertThat(result.newAssignments).containsExactly(User.UserId(3L))
        assertThat(result.skippedStudents).hasSize(2)
        assertThat(result.skippedStudents).containsExactlyInAnyOrder(
            User.UserId(2L), User.UserId(4L)
        )
        assertThat(result.hasNewAssignments()).isTrue()
        assertThat(result.getAssignmentCount()).isEqualTo(1)
        assertThat(result.getSkippedCount()).isEqualTo(2)
    }

    @Test
    fun `모든 학생이 이미 출제받은 경우 새로운 출제가 없다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(User.UserId(2L), User.UserId(3L))
        val alreadyAssignedIds = listOf(User.UserId(2L), User.UserId(3L))

        val result = piece.assignToStudents(
            requestTeacherId = 1L,
            studentIds = studentIds,
            alreadyAssignedStudentIds = alreadyAssignedIds
        )

        assertThat(result.newAssignments).isEmpty()
        assertThat(result.skippedStudents).hasSize(2)
        assertThat(result.skippedStudents).containsExactlyInAnyOrder(
            User.UserId(2L), User.UserId(3L)
        )
        assertThat(result.hasNewAssignments()).isFalse()
        assertThat(result.getAssignmentCount()).isEqualTo(0)
        assertThat(result.getSkippedCount()).isEqualTo(2)
    }

    @Test
    fun `학습지를 만든 선생이 아닌 경우 예외가 발생한다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(User.UserId(2L), User.UserId(3L))
        val alreadyAssignedIds = emptyList<User.UserId>()

        assertThatThrownBy {
            piece.assignToStudents(
                requestTeacherId = 2L, // 다른 선생님 ID
                studentIds = studentIds,
                alreadyAssignedStudentIds = alreadyAssignedIds
            )
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Only the teacher who created this piece can assign it to students")
    }

    @Test
    fun `학생 ID 목록이 비어있는 경우 예외가 발생한다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = emptyList<User.UserId>()
        val alreadyAssignedIds = emptyList<User.UserId>()

        assertThatThrownBy {
            piece.assignToStudents(
                requestTeacherId = 1L,
                studentIds = studentIds,
                alreadyAssignedStudentIds = alreadyAssignedIds
            )
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Student IDs cannot be empty")
    }

    @Test
    fun `단일 학생에게 출제할 때 정상적으로 처리된다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(User.UserId(2L))
        val alreadyAssignedIds = emptyList<User.UserId>()

        val result = piece.assignToStudents(
            requestTeacherId = 1L,
            studentIds = studentIds,
            alreadyAssignedStudentIds = alreadyAssignedIds
        )

        assertThat(result.newAssignments).hasSize(1)
        assertThat(result.newAssignments).containsExactly(User.UserId(2L))
        assertThat(result.skippedStudents).isEmpty()
        assertThat(result.getAssignmentCount()).isEqualTo(1)
        assertThat(result.getSkippedCount()).isEqualTo(0)
    }

    @Test
    fun `이미 출제받은 학생에게 다시 출제하려고 하면 스킵된다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(User.UserId(2L))
        val alreadyAssignedIds = listOf(User.UserId(2L))

        val result = piece.assignToStudents(
            requestTeacherId = 1L,
            studentIds = studentIds,
            alreadyAssignedStudentIds = alreadyAssignedIds
        )

        assertThat(result.newAssignments).isEmpty()
        assertThat(result.skippedStudents).hasSize(1)
        assertThat(result.skippedStudents).containsExactly(User.UserId(2L))
        assertThat(result.hasNewAssignments()).isFalse()
        assertThat(result.getAssignmentCount()).isEqualTo(0)
        assertThat(result.getSkippedCount()).isEqualTo(1)
    }

    @Test
    fun `중복된 학생 ID가 있어도 정상적으로 처리된다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(User.UserId(2L), User.UserId(3L), User.UserId(2L)) // 중복
        val alreadyAssignedIds = emptyList<User.UserId>()

        val result = piece.assignToStudents(
            requestTeacherId = 1L,
            studentIds = studentIds,
            alreadyAssignedStudentIds = alreadyAssignedIds
        )

        // 중복을 제거하지 않고 그대로 처리하므로 3개가 반환됨
        assertThat(result.newAssignments).hasSize(3)
        assertThat(result.newAssignments).containsExactly(
            User.UserId(2L), User.UserId(3L), User.UserId(2L)
        )
        assertThat(result.skippedStudents).isEmpty()
    }

    @Test
    fun `부분적으로 중복된 학생들이 있는 복잡한 케이스를 처리한다`() {
        val piece = createPiece(pieceId = 1L, teacherId = 1L)
        val studentIds = listOf(
            User.UserId(2L), User.UserId(3L), User.UserId(4L),
            User.UserId(5L), User.UserId(6L)
        )
        val alreadyAssignedIds = listOf(User.UserId(2L), User.UserId(5L))

        val result = piece.assignToStudents(
            requestTeacherId = 1L,
            studentIds = studentIds,
            alreadyAssignedStudentIds = alreadyAssignedIds
        )

        assertThat(result.newAssignments).hasSize(3)
        assertThat(result.newAssignments).containsExactlyInAnyOrder(
            User.UserId(3L), User.UserId(4L), User.UserId(6L)
        )
        assertThat(result.skippedStudents).hasSize(2)
        assertThat(result.skippedStudents).containsExactlyInAnyOrder(
            User.UserId(2L), User.UserId(5L)
        )
        assertThat(result.getAssignmentCount()).isEqualTo(3)
        assertThat(result.getSkippedCount()).isEqualTo(2)
    }

    private fun createPiece(pieceId: Long, teacherId: Long): Piece {
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
        return Piece(
            pieceId = Piece.PieceId(pieceId),
            name = "테스트 학습지",
            teacherId = teacherId,
            problems = problems
        )
    }
}
