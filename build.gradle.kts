plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // --- Tambahan untuk Security & JWT ---
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly  ("io.jsonwebtoken:jjwt-impl:0.11.5")      // implementasi JJWT
    runtimeOnly  ("io.jsonwebtoken:jjwt-jackson:0.11.5")    // untuk serialisasi JSON

    // ───────────────────────────────────────────────────────────────
    // Tambahkan ini untuk JPA & PostgreSQL:
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Jika kamu memang perlu override versi Spring Data JPA:
    implementation("org.springframework.data:spring-data-jpa:3.4.2")
    // JDBC driver PostgreSQL:
    implementation("org.postgresql:postgresql:42.6.0")
    // 1) Spring Data JPA starter (brings in jakarta.persistence)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // ───────────────────────────────────────────────────────────────


    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required = true
        xml.required = true
    }
}