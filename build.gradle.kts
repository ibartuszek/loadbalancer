import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

group = "org.ibartuszek.loadbalance"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.4")
    testImplementation(kotlin("test"))
    testImplementation("org.mockito:mockito-core:4.8.1")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("Main")
}