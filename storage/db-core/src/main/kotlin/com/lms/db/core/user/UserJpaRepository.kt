package com.lms.db.core.user

import com.lms.core_common.enum.UserType
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByType(type: UserType): List<UserEntity>
    fun findAllByTypeAndIdIn(type: UserType, ids: List<Long>): List<UserEntity>
}
