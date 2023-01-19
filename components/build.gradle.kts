
buildscript {
    repositories {
        google()
        mavenCentral()

        if (!libs.versions.compose.snapshot.get().endsWith("SNAPSHOT")) {
            maven { url = uri("https://androidx.dev/snapshots/builds/${libs.versions.compose.snapshot.get()}/artifacts/repository/") }
        }
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }
}
// Required by the "version catalog update" plugin to help keep
// the versions in the Gradle version catalog toml file up to date.
plugins {
    id("com.github.ben-manes.versions") version "0.43.0"
    id("nl.littlerobots.version-catalog-update") version "0.7.0"
    id("com.android.library") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.21" apply false
}

apply("${project.rootDir}/buildscripts/toml-updater-config.gradle")