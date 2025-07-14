package com.lms.core_domain.user.service

import com.lms.core_common.enum.UserType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.user.domain.User
import com.lms.core_domain.user.domain.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserFinderTest {

    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var userFinder: UserFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        userFinder = UserFinder(userRepository)
    }

    @Test
    fun `모든 학생이 존재할 때 검증이 성공한다`() {
        val studentIds = listOf(User.UserId(1L), User.UserId(2L))
        val students = listOf(
            User(
                userId = User.UserId(1L),
                email = "student1@test.com",
                username = "student1",
                password = "password",
                firstName = "Student",
                lastName = "One",
                type = UserType.STUDENT
            ),
            User(
                userId = User.UserId(2L),
                email = "student2@test.com",
                username = "student2",
                password = "password",
                firstName = "Student",
                lastName = "Two",
                type = UserType.STUDENT
            )
        )

        every { userRepository.findByIdIn(studentIds) } returns students

        userFinder.validateStudentsExist(studentIds)

        verify { userRepository.findByIdIn(studentIds) }
    }

    @Test
    fun `존재하지 않는 학생이 있을 때 BusinessException이 발생한다`() {
        val studentIds = listOf(User.UserId(1L), User.UserId(999L))
        val existingStudents = listOf(
            User(
                userId = User.UserId(1L),
                email = "student1@test.com",
                username = "student1",
                password = "password",
                firstName = "Student",
                lastName = "One",
                type = UserType.STUDENT
            )
        )

        every { userRepository.findByIdIn(studentIds) } returns existingStudents

        assertThatThrownBy { userFinder.validateStudentsExist(studentIds) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Students not found: [999]")

        verify { userRepository.findByIdIn(studentIds) }
    }

    @Test
    fun `빈 학생 목록으로 검증할 때 성공한다`() {
        val emptyStudentIds = emptyList<User.UserId>()

        every { userRepository.findByIdIn(emptyStudentIds) } returns emptyList()

        userFinder.validateStudentsExist(emptyStudentIds)

        verify { userRepository.findByIdIn(emptyStudentIds) }
    }
}
