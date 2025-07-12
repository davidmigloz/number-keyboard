import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import versioning.generateVersionCode
import versioning.generateVersionName

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val versionMajor = 2 // API Changes, adding big new feature, redesign the App
val versionMinor = 0 // New features in a backwards-compatible manner
val versionPatch = 0 // Backwards-compatible bug fixes
val versionClassifier: String? = null // Pre-releases (alpha, beta, rc, SNAPSHOT...)

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.valueOf("JVM_${libs.versions.jvm.get()}"))
        }
    }
    jvmToolchain(libs.versions.jvm.get().toInt())

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":lib"))
                implementation(project.dependencies.platform(libs.compose.bom))
                implementation(compose.material3)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)

                implementation(libs.androidx.navigation.compose)
            }
        }
    }
}

android {
    namespace = "com.davidmiguel.numberkeyboard.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    // Remove once migrated to KMP
    sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")

    buildFeatures {
        compose = true
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