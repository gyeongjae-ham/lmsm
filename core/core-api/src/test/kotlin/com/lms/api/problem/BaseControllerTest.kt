package com.lms.api.problem

import com.lms.config.GlobalExceptionHandler
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

abstract class BaseControllerTest {

    protected lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUpMockMvc() {
        MockKAnnotations.init(this)

        mockMvc = MockMvcBuilders.standaloneSetup(*getControllers())
            .addPlaceholderValue("api.base-path", "/api/v1")
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    protected abstract fun getControllers(): Array<Any>
}
