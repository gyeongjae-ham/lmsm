package com.lms.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@ConfigurationPropertiesScan
@SpringBootApplication
@ComponentScan(basePackages = ["com.lms"])
class LMSApplication

fun main(args: Array<String>) {
	runApplication<LMSApplication>(*args)
}
