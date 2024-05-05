val kspVersion: String by project

plugins {
    kotlin("jvm")
}

group = "org.team9432.lib"

dependencies {
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}

repositories {
    mavenCentral()
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

