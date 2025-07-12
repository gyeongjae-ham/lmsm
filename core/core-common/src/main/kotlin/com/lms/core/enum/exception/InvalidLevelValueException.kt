package com.lms.core.enum.exception

import com.lms.core.exception.BusinessException

class InvalidLevelValueException(
    levelValue: Int,
    message: String = "Invalid level value $levelValue"
) : BusinessException(message)
