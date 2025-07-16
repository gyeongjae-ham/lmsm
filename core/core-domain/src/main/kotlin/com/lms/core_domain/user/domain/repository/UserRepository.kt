package com.lms.core_domain.user.domain.repository

import com.lms.core_common.enum.UserType
import com.lms.core_domain.user.domain.User

interface UserRepository {
    fun findByType(type: UserType): List<User>
    fun findByIdIn(studentIds: List<User.UserId>): List<User>
}
