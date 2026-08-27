import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.androidx.room)
}

val configProperties = Properties()
project.file("config.properties").inputStream().use { configProperties.load(it) }

android {
    namespace = "eu.meecolabs.heshunt"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "eu.meecolabs.heshunt"
        minSdk = 28
        targetSdk = 37
        versionCode = project.file("version.txt").readText().trim().toInt()
        versionName = SimpleDateFormat("yyyy.MM.dd").format(Date())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //noinspection WrongGradleMethod
        configProperties.forEach { (key, value) ->
            val propertyKey = key.toString()
            if (propertyKey.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))) {
                val propertyValue = value.toString().replace("\"", "\\\"")
                buildConfigField("String", propertyKey, "\"$propertyValue\"")
            } else {
                throw Exception("Warning: Property key '$propertyKey' from settings.properties is not a valid Java identifier. Skipping BuildConfig field generation for this key.")
            }
        }
    }

    signingConfigs {
        create("release") {
            storeFile = System.getenv("ANDROID_KEYSTORE_FILE")?.let { rootProject.file(it) }
            storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ANDROID_KEY_ALIAS")
            keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )

            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        disable += "ModifierParameter"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)

    // Splashscreen
    implementation(libs.androidx.splashscreen)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Material
    implementation(libs.androidx.compose.material3)

    // Navigation
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Serialization
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // Koin/DI
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.navigation3)
    implementation(libs.koin.annotations)
    implementation(libs.koin.androidx.workmanager)

    // App Updates
    implementation(libs.meecolabs.appupdates)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.testing)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // OKHttp
    implementation(libs.logging.interceptor)

    // MapLibre
    implementation(libs.maplibre.compose)
    runtimeOnly(libs.maplibre.compose.runtime.vulkan)
    implementation(libs.maplibre.compose.material3)
    implementation(libs.maplibre.spatialk.gpx)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

tasks.register("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}
