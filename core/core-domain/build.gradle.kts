dependencies {
    implementation(project(":core:core-common"))
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("io.mockk:mockk:${property("mockkVersion")}")
    testImplementation("io.mockk:mockk-jvm:${property("mockkVersion")}")
}
