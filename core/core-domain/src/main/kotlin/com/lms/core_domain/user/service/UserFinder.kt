package com.lms.core_domain.user.service

import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.user.domain.User
import com.lms.core_domain.user.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserFinder(
    private val userRepository: UserRepository
) {
    fun validateStudentsExist(studentIds: List<User.UserId>) {
        val users = userRepository.findByIdIn(studentIds)

        val existingUserIds = users.map { it.id }
        val missingUserIds = studentIds.filter { !existingUserIds.contains(it) }
        if (missingUserIds.isNotEmpty()) {
            throw BusinessException("Students not found: ${missingUserIds.map { it.value }}")
        }
    }

    fun getStudentsByIds(studentIds: List<User.UserId>): List<User> {
        val users = userRepository.findByIdIn(studentIds)

        val existingUserIds = users.map { it.id }
        val missingUserIds = studentIds.filter { !existingUserIds.contains(it) }
        if (missingUserIds.isNotEmpty()) {
            throw BusinessException("Students not found: ${missingUserIds.map { it.value }}")
        }

        return users
    }
}
