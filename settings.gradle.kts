import java.util.*

pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    val frcYear: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
    }

    repositories {
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
props.setProperty("org.gradle.internal.native.headers.unresolved.dependencies.ignore", "true");

rootProject.name = "robotbase"

include(":robot")
include(":annotation")