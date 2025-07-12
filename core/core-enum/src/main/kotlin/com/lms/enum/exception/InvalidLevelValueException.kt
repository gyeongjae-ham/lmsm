package com.lms.enum.exception

import org.apache.coyote.BadRequestException

class InvalidLevelValueException(
    levelValue: Int,
    message: String = "Invalid level value $levelValue"
) : BadRequestException(message)
