import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import versioning.generateVersionName

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val versionMajor = 4 // API Changes, adding big new feature, redesign the App
val versionMinor = 0 // New features in a backwards-compatible manner
val versionPatch = 8 // Backwards-compatible bug fixes
val versionClassifier: String? = null // Pre-releases (alpha, beta, rc, SNAPSHOT...)

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
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

    jvmToolchain(libs.versions.jvm.get().toInt())

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.compose.bom))
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
        }
    }
}

android {
    namespace = "com.davidmigloz.numberkeyboard"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        version = generateVersionName(versionMajor, versionMinor, versionPatch, versionClassifier)

        consumerProguardFiles("proguard-rules.pro")
    }
    resourcePrefix = "numberkeyboard_"
}