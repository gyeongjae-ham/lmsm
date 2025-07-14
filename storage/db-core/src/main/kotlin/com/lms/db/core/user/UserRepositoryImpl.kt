package com.lms.db.core.user

import com.lms.core_common.enum.UserType
import com.lms.core_domain.user.domain.User
import com.lms.core_domain.user.domain.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {

    override fun findByType(type: UserType): List<User> {
        return userJpaRepository.findByType(type).map { it.toDomain() }
    }

    override fun findByIdIn(studentIds: List<User.UserId>): List<User> {
        return userJpaRepository
            .findAllByTypeAndIdIn(type = UserType.STUDENT, ids = studentIds.map { it.value })
            .map { it.toDomain() }
    }
}

fun UserEntity.toDomain(): User {
    return User(
        userId = this.id?.let { User.UserId(it) },
        email = this.email,
        username = this.username,
        password = this.password,
        firstName = this.firstName,
        lastName = this.lastName,
        type = this.type
    )
}
