plugins {
    kotlin("jvm") version "1.9.23"
    id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
}

group = "me.koendev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project
val kotlin_version: String by project

val exposed_version: String by project
val h2_version: String by project

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    implementation("io.ktor:ktor-client-serialization:2.3.7")
    implementation("com.zaxxer:HikariCP:5.0.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}