val ktorVersion: String by project
val kotlinVersion: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "org.team9432.dashboard.lib"

dependencies {
    implementation(project(":dashboard-shared"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
}

repositories {
    mavenCentral()
}


sourceSets.main {
    java.srcDirs("src/main/kotlin")
}