tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-common"))
    runtimeOnly(project(":storage:db-core"))
    compileOnly(project(":storage:db-core"))
    runtimeOnly(project(":support:monitoring"))
    compileOnly(project(":support:monitoring"))

    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("io.mockk:mockk:${property("mockkVersion")}")
    testImplementation("io.mockk:mockk-jvm:${property("mockkVersion")}")
}
