plugins {
    kotlin("jvm") version "1.9.23"
    id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
}

group = "me.koendev"
version = "1.0"

repositories {
    mavenCentral()
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes("Main-Class" to "me.koendev.MainKt")
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

val exposedVersion: String by project

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
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