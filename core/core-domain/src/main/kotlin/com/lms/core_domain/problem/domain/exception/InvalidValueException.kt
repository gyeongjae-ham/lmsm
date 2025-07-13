package com.lms.core_domain.problem.domain.exception

import com.lms.core_common.exception.BusinessException

class InvalidValueException(
    message: String = "Invalid value exception",
) : BusinessException(message)
