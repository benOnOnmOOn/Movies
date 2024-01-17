@file:Suppress("UnstableApiUsage")

plugins {
    embeddedKotlin("android")
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.firebase.crashlytics.gradle) apply false
    alias(libs.plugins.firebase.perf.gradle) apply false
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bz.movies"

    defaultConfig {
        applicationId = "com.bz.movies"
        versionCode = 1
        versionName = "1.0"
    }

    androidResources.generateLocaleConfig = true

    buildTypes {
        release {
            apply(plugin = "com.google.gms.google-services")
            apply(plugin = "com.google.firebase.crashlytics")
            apply(plugin = "com.google.firebase.firebase-perf")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

//            optimization {
//                keepRules {
//                    ignoreAllExternalDependencies(true)
//                }
//            }
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencyAnalysis {
    issues { onUnusedDependencies { exclude(":presentation:core") } }
}

dependencies {
    // don't warn
    implementation(project(":presentation:core"))
    kover(project(":presentation:core"))
    kover(project(":presentation:screens"))
    kover(project(":data:network"))
    kover(project(":data:database"))
    kover(project(":data:dto"))

    implementation(libs.kotlin.stdlib)

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.perf)

    //  HILT
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    //

    implementation(libs.androidx.startup.runtime)

    implementation(libs.timber)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
}
