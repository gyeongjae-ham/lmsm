package com.lms.db.core.studentpiece

import com.lms.core_common.enum.UserType
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.studentpiece.domain.StudentPiece
import com.lms.core_domain.user.domain.User
import com.lms.db.core.BaseRepositoryTest
import com.lms.db.core.piece.PieceEntity
import com.lms.db.core.piece.PieceJpaRepository
import com.lms.db.core.user.UserEntity
import com.lms.db.core.user.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class StudentPieceRepositoryImplTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var studentPieceJpaRepository: StudentPieceJpaRepository

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var pieceJpaRepository: PieceJpaRepository

    private lateinit var studentPieceRepository: StudentPieceRepositoryImpl

    @BeforeEach
    fun setup() {
        studentPieceRepository = StudentPieceRepositoryImpl(studentPieceJpaRepository)

        studentPieceJpaRepository.deleteAll()
        pieceJpaRepository.deleteAll()
        userJpaRepository.deleteAll()
    }

    @Test
    fun `StudentPiece 목록을 정상적으로 저장한다`() {
        val userEntity = UserEntity(
            email = "student0@test.com",
            username = "student0",
            password = "password",
            firstName = "Test",
            lastName = "Student",
            type = UserType.STUDENT
        )
        val savedUser = userJpaRepository.save(userEntity)

        val pieceEntity = PieceEntity(
            name = "테스트 학습지",
            teacherId = 1L
        )
        val savedPiece = pieceJpaRepository.save(pieceEntity)

        val studentPieces = listOf(
            StudentPiece.assign(User.UserId(savedUser.id!!), Piece.PieceId(savedPiece.id!!))
        )

        val result = studentPieceRepository.saveAll(studentPieces)

        assertThat(result).hasSize(1)
        assertThat(result[0].getStudentId().value).isEqualTo(savedUser.id)
        assertThat(result[0].getPieceId().value).isEqualTo(savedPiece.id)

        val savedEntities = studentPieceJpaRepository.findAll()
        assertThat(savedEntities).hasSize(1)
        assertThat(savedEntities[0].studentId).isEqualTo(savedUser.id)
        assertThat(savedEntities[0].pieceId).isEqualTo(savedPiece.id)
    }

    @Test
    fun `이미 출제된 학생들을 정상적으로 조회한다`() {
        val userEntities = listOf(
            UserEntity(
                email = "student1@test.com",
                username = "student1",
                password = "password",
                type = UserType.STUDENT
            ),
            UserEntity(
                email = "student2@test.com",
                username = "student2",
                password = "password",
                type = UserType.STUDENT
            ),
            UserEntity(
                email = "student3@test.com",
                username = "student3",
                password = "password",
                type = UserType.STUDENT
            )
        )
        val savedUsers = userJpaRepository.saveAll(userEntities)

        val pieceEntity = PieceEntity(
            name = "테스트 학습지",
            teacherId = 1L
        )
        val savedPiece = pieceJpaRepository.save(pieceEntity)

        val studentPieceEntities = listOf(
            StudentPieceEntity(
                studentId = savedUsers[0].id!!,
                pieceId = savedPiece.id!!
            ),
            StudentPieceEntity(
                studentId = savedUsers[2].id!!,
                pieceId = savedPiece.id!!
            )
        )
        studentPieceJpaRepository.saveAll(studentPieceEntities)

        val studentIds = savedUsers.map { User.UserId(it.id!!) }
        val pieceId = Piece.PieceId(savedPiece.id!!)

        val result = studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId)

        assertThat(result).hasSize(2)
        val resultStudentIds = result.map { it.getStudentId().value }
        assertThat(resultStudentIds).containsExactlyInAnyOrder(savedUsers[0].id, savedUsers[2].id)
    }

    @Test
    fun `출제된 학생이 없을 때 빈 목록을 반환한다`() {
        val userEntity = UserEntity(
            email = "student4@test.com",
            username = "student4",
            password = "password",
            type = UserType.STUDENT
        )
        val savedUser = userJpaRepository.save(userEntity)

        val pieceEntity = PieceEntity(
            name = "테스트 학습지",
            teacherId = 1L
        )
        val savedPiece = pieceJpaRepository.save(pieceEntity)

        val studentIds = listOf(User.UserId(savedUser.id!!))
        val pieceId = Piece.PieceId(savedPiece.id!!)

        val result = studentPieceRepository.findByStudentIdsAndPieceId(studentIds, pieceId)

        assertThat(result).isEmpty()
    }
}
