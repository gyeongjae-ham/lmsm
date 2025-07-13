package com.lms.core_common.exception

import org.springframework.http.HttpStatus

open class BusinessException(
    override val message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message)
