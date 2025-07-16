package com.lms.core_domain.user.domain

import com.lms.core_common.enum.UserType

class User(
    private val userId: UserId? = null,
    email: String,
    username: String,
    password: String,
    firstName: String?,
    lastName: String?,
    type: UserType
) {
    val id: UserId
        get() = requireNotNull(userId) { "User ID is null" }

    data class UserId(val value: Long)
}
