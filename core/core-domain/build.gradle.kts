dependencies {
    implementation(project(":core:core-common"))

    testImplementation("io.mockk:mockk:${property("mockkVersion")}")
    testImplementation("io.mockk:mockk-jvm:${property("mockkVersion")}")
}
