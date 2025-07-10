import versioning.generateVersionCode
import versioning.generateVersionName

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val versionMajor = 2 // API Changes, adding big new feature, redesign the App
val versionMinor = 0 // New features in a backwards-compatible manner
val versionPatch = 0 // Backwards-compatible bug fixes
val versionClassifier: String? = null // Pre-releases (alpha, beta, rc, SNAPSHOT...)

android {
    namespace = "com.davidmiguel.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        compose = true
    }

    kotlin {
        jvmToolchain(libs.versions.jvm.get().toInt())
    }

    defaultConfig {
        applicationId = "com.davidmiguel.numberkeyboard.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = generateVersionCode(versionMajor, versionMinor, versionPatch)
        versionName = generateVersionName(versionMajor, versionMinor, versionPatch, versionClassifier)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":lib"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.nav)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
}