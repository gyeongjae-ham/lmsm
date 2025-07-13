allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-common"))
    implementation("org.flywaydb:flyway-core")
    implementation("com.h2database:h2")
    testImplementation("com.h2database:h2")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
}
