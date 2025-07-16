import versioning.generateVersionName

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
}

val versionMajor = 5 // API Changes, adding big new feature, redesign the App
val versionMinor = 0 // New features in a backwards-compatible manner
val versionPatch = 2 // Backwards-compatible bug fixes
val versionClassifier: String? = null // Pre-releases (alpha, beta, rc, SNAPSHOT...)

kotlin {
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "NumberKeyboard"
            isStatic = true
        }
    }
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.compose.bom))
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
        }
    }
}

android {
    namespace = "com.davidmiguel.numberkeyboard"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        version = generateVersionName(versionMajor, versionMinor, versionPatch, versionClassifier)

        consumerProguardFiles("proguard-rules.pro")
    }
    publishing {
        singleVariant("release")
    }
    resourcePrefix = "numberkeyboard_"
}