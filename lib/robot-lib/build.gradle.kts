val kspVersion: String by project
val wpiLibVersion: String by project
val frcYear: String by project

plugins {
    kotlin("jvm")
}

group = "org.team9432.lib"

dependencies {
    // Wpilib
    implementation("edu.wpi.first.hal:hal-java:$wpiLibVersion")
    implementation("edu.wpi.first.wpilibj:wpilibj-java:$wpiLibVersion")
    implementation("edu.wpi.first.wpiutil:wpiutil-java:$wpiLibVersion")
    implementation("edu.wpi.first.wpimath:wpimath-java:$wpiLibVersion")
    implementation("edu.wpi.first.ntcore:ntcore-jni:$wpiLibVersion")
    implementation("edu.wpi.first.ntcore:ntcore-java:$wpiLibVersion")

    // Vendors
    implementation("edu.wpi.first.wpilibNewCommands:wpilibNewCommands-java:$wpiLibVersion")
    implementation("com.ctre.phoenix6:wpiapi-java:24.2.0")
    implementation("org.littletonrobotics.akit.junction:wpilib-shim:3.0.2")
    implementation("org.littletonrobotics.akit.junction:junction-core:3.0.2")
    implementation("org.littletonrobotics.akit.conduit:conduit-api:3.0.2")
    implementation("org.photonvision:photonlib-java:v2024.3.1")
    implementation("org.photonvision:photontargeting-java:v2024.3.1")
    implementation("com.revrobotics.frc:REVLib-java:2024.2.1")

    // Misc.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
}

repositories {
    maven { setUrl("https://frcmaven.wpi.edu/artifactory/release/") }
    maven { setUrl("https://plugins.gradle.org/m2/") }
    maven { setUrl("https://maven.ctr-electronics.com/release/") }
    maven { setUrl("https://maven.revrobotics.com/") }
    maven { setUrl("https://maven.photonvision.org/repository/internal") }
    maven { setUrl("https://maven.photonvision.org/repository/snapshots") }

    maven {
        url = uri("https://maven.pkg.github.com/Mechanical-Advantage/AdvantageKit")
        credentials {
            username = "Mechanical-Advantage-Bot"
            password =
                "\u0067\u0068\u0070\u005f\u006e\u0056\u0051\u006a\u0055\u004f\u004c\u0061\u0079\u0066\u006e\u0078\u006e\u0037\u0051\u0049\u0054\u0042\u0032\u004c\u004a\u006d\u0055\u0070\u0073\u0031\u006d\u0037\u004c\u005a\u0030\u0076\u0062\u0070\u0063\u0051"
        }
    }
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

