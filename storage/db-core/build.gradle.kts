allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":core:core-domain"))
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("com.h2database:h2")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
}
