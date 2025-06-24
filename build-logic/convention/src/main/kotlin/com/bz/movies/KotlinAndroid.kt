package com.bz.movies

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import kotlin.collections.plusAssign
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun ApplicationExtension.baseConfig(project: Project) {
    defaultBaseConfig(project)
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
internal fun CommonExtension<*, *, *, *, *, *>.defaultBaseConfig(project: Project) {
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        minSdk = 27
        resourceConfigurations += listOf("pl", "en")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

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

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroidApp(commonExtension: ApplicationExtension) {
    commonExtension.baseConfig(this)
    configureKotlin<KotlinAndroidProjectExtension>()
}

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.defaultBaseConfig(this)
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
        jvmTarget = JvmTarget.JVM_21
        allWarningsAsErrors = false
        progressiveMode = true
    }
}
