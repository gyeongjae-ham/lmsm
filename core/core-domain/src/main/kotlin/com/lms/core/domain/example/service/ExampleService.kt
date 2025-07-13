package com.lms.core.domain.example.service

import com.lms.core.domain.example.dao.ExampleDAO
import com.lms.core.domain.example.domain.ExampleDomain
import org.springframework.stereotype.Service

@Service
class ExampleService(
    private val exampleDAO: ExampleDAO,
) {
    fun example(): ExampleDomain {
        return ExampleDomain()
    }
}
