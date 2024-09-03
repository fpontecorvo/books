import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.graalvm.buildtools.native") version "0.10.1"
    id("org.jetbrains.kotlin.plugin.noarg") version "2.0.20"
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
}

group = "com.library"
version = "1.0.0"
java.sourceCompatibility = VERSION_21

repositories {
    mavenCentral()
}

val springCloudVersion = "2022.0.3"
val openapiVersion = "2.6.0"
val arrowVersion = "1.2.3"
val kotestVersion = "5.5.5"
val kotestArrowVersion = "1.3.0"
val mockkVersion = "1.13.4"

dependencies {

    // KOTLIN
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // SPRING
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.kafka:spring-kafka")

    // ARROW
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    // TRACING
    implementation("io.micrometer:micrometer-tracing-bridge-otel")

    // SWAGGER
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:$openapiVersion")

    // JACKSON
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // TEST
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:$kotestArrowVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("installGitHook") {
    group = "verification"
    description = "install pre-commit & pre-push linting hooks"

    // copy pre-commit hook
    from("scripts/pre-commit")
    into(".git/hooks")

    // copy pre-push hook
    from("scripts/pre-push")
    into(".git/hooks")

    fileMode = 0b111111101
}

tasks.build {
    dependsOn("installGitHook")
}

tasks.bootJar {
    archiveFileName.set("api.jar")
}
