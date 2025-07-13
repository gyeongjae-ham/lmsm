package com.lms.core_domain.enum.exception

import com.lms.core_domain.exception.BusinessException

class InvalidLevelValueException(
    levelValue: Int,
    message: String = "Invalid level value $levelValue"
) : BusinessException(message)
