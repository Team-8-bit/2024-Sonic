import java.util.*

pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    val frcYear: String by settings
    val composeVersion: String by settings
    val kotlinxSerializationVersion: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.compose") version composeVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinxSerializationVersion
    }

    repositories {
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()

        val frcHome = if (System.getProperty("os.name").contains("windows")) {
            val publicFolder = System.getenv("PUBLIC") ?: "C:\\Users\\Public"
            val homeRoot = File(publicFolder, "wpilib")
            File(homeRoot, frcYear)
        } else {
            val userFolder = System.getProperty("user.home")
            val homeRoot = File(userFolder, "wpilib")
            File(homeRoot, frcYear)
        }
        val frcHomeMaven = File(frcHome, "maven")

        maven {
            name = "frcHome"
            url = frcHomeMaven.toURI()
        }
    }
}

val props: Properties = System.getProperties()
props.setProperty("org.gradle.internal.native.headers.unresolved.dependencies.ignore", "true")

rootProject.name = "2024-Sonic"

include(":robot")
include(":annotation")
include(":dashboard")
include(":dashboard-app")
