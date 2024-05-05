pluginManagement {
    val kotlinVersion: String by settings
    val composeVersion: String by settings
    val kotlinxSerializationVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.compose") version composeVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinxSerializationVersion
    }

    repositories {
        mavenCentral()
    }
}

rootProject.name = "dashboard"

include(":dashboard-lib")
include(":dashboard-app")
include(":dashboard-shared")
