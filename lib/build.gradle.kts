import versioning.generateVersionName

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

val versionMajor = 4 // API Changes, adding big new feature, redesign the App
val versionMinor = 0 // New features in a backwards-compatible manner
val versionPatch = 8 // Backwards-compatible bug fixes
val versionClassifier: String? = null // Pre-releases (alpha, beta, rc, SNAPSHOT...)

android {
    namespace = "com.davidmiguel.numberkeyboard"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        compose = true
    }

    kotlin {
        jvmToolchain(libs.versions.jvm.get().toInt())
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        version = generateVersionName(versionMajor, versionMinor, versionPatch, versionClassifier)

        consumerProguardFiles("proguard-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    resourcePrefix = "numberkeyboard_"
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
}