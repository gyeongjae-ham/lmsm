package com.lms.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

    @Value("\${spring.profiles.active:local}")
    private lateinit var activeProfile: String

    /**
     * CORS 설정
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        val allowedOrigins = when (activeProfile) {
            "local", "dev" -> arrayOf("http://localhost:3000", "http://localhost:8080")
            "prod" -> arrayOf("https://lms.com")
            else -> arrayOf("http://localhost:3000")
        }

        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
