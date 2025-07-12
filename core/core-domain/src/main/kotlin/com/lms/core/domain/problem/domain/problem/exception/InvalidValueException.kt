package com.lms.core.domain.problem.domain.problem.exception

import com.lms.core.exception.BusinessException

class InvalidValueException(
    message: String = "Invalid value exception",
) : BusinessException(message)
