val ktorVersion: String by project
val kotlinVersion: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "org.team9432.lib.dashboard"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.10.1")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.735")
}

repositories {
    mavenCentral()
}


sourceSets.main {
    java.srcDirs("src/main/kotlin")
}