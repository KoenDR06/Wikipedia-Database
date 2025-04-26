plugins {
    kotlin("jvm") version "1.9.23"
    id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
}

group = "me.koendev"
version = "1.0.1"

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
    implementation(files("../Utils-latest.jar"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}