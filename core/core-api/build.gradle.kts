tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    implementation(project(":core:core-domain"))
    runtimeOnly(project(":storage:db-core"))
    compileOnly(project(":storage:db-core"))
    runtimeOnly(project(":support:monitoring"))
    compileOnly(project(":support:monitoring"))
}
