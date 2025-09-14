package com.bz.movies

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import kotlin.collections.plusAssign
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun ApplicationExtension.baseAppConfig() {
    defaultBaseConfig()
    dependenciesInfo.apply {
        includeInApk = false
        includeInBundle = false
    }

    @Suppress("UnstableApiUsage", "MissingResourcesProperties")
    androidResources.generateLocaleConfig = true

    defaultConfig {
        applicationId = "com.bz.movies"
        versionCode = 1
        versionName = "1.0"

        targetSdk = 35
        multiDexEnabled = false
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

//            optimization {
//                keepRules {
//                    ignoreAllExternalDependencies(true)
//                }
//            }
            proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions.isCoreLibraryDesugaringEnabled = false
}

//region Global android configuration
internal fun CommonExtension.defaultBaseConfig() {
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    packaging.resources.excludes +=
        setOf(
            "kotlin/**",
            "META-INF/**",
            "META-INF/services/**",
            "**.properties",
            "kotlin-tooling-metadata.json",
            "DebugProbesKt.bin"
        )
}

internal fun LibraryExtension.defaultBaseLibConfig() {
    defaultConfig {
        minSdk = 27
        resourceConfigurations += listOf("pl", "en")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

internal fun TestExtension.defaultBaseTestConfig() {
    defaultConfig {
        minSdk = 27
        resourceConfigurations += listOf("pl", "en")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroidApp(commonExtension: ApplicationExtension) {
    commonExtension.baseAppConfig()
    configureKotlin<KotlinAndroidProjectExtension>()
}

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(commonExtension: LibraryExtension) {
    commonExtension.defaultBaseConfig()
    commonExtension.defaultBaseLibConfig()
    configureKotlin<KotlinAndroidProjectExtension>()
}

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinTestAndroid(commonExtension: TestExtension) {
    commonExtension.defaultBaseConfig()
    commonExtension.defaultBaseTestConfig()
    configureKotlin<KotlinAndroidProjectExtension>()
}


/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    configureKotlin<KotlinJvmProjectExtension>()
}

/**
 * Configure base Kotlin options
 */
private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() = configure<T> {
    when (this) {
        is KotlinAndroidProjectExtension -> compilerOptions
        is KotlinJvmProjectExtension -> compilerOptions
        else -> TODO("Unsupported project extension $this ${T::class}")
    }.apply {
        jvmDefault = JvmDefaultMode.NO_COMPATIBILITY
        progressiveMode = true
        jvmTarget = JvmTarget.JVM_21
        allWarningsAsErrors = false
        progressiveMode = true
        explicitApi = ExplicitApiMode.Strict
    }
}
