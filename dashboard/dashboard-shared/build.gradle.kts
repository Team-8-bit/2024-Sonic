val ktorVersion: String by project
val kotlinVersion: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "org.team9432.dashboard"

dependencies {
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

repositories {
    mavenCentral()
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}