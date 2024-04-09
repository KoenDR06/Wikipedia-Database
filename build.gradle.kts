plugins {
    kotlin("jvm") version "1.9.23"
    id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
}

group = "me.koendev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    implementation("io.ktor:ktor-client-serialization:2.3.7")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}