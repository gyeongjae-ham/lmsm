package com.lms.core_common.enum.exception

import com.lms.core_common.exception.BusinessException

class InvalidLevelValueException(
    levelValue: Int,
    message: String = "Invalid level value $levelValue"
) : BusinessException(message)
