package com.lms.api.example.controller

import com.lms.core.domain.example.service.ExampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExampleController(
    private val exampleService: ExampleService,
) {

    @GetMapping
    fun example(): String {
        return "Hello, World!"
    }
}
