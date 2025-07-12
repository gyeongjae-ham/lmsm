package com.lms.core.domain.problem.domain.exception

import org.apache.coyote.BadRequestException

class InvalidValueException(
    message: String = "Invalid value exception",
) : BadRequestException(message)
