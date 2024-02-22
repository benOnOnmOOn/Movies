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
    alias(libs.plugins.dependency.guard)
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
                "proguard-rules.pro"
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
    issues {
        onUnusedDependencies { exclude(":presentation:core") }
        onIncorrectConfiguration { exclude("org.jetbrains.kotlin:kotlin-stdlib") }
    }
}

dependencies {
    // don't warn
    implementation(project(":presentation:core"))
    kover(project(":presentation:core"))
    kover(project(":presentation:screens"))
    kover(project(":data:network"))
    kover(project(":data:database"))

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.perf)

    //  HILT
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    //

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.annotation)
    implementation(libs.dagger)
    implementation(libs.javax.inject)
    implementation(libs.okhttp)
    implementation(libs.timber)

    releaseImplementation(libs.guava)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
}

configurations {
    implementation {
        exclude("com.google.guava", "failureaccess")
        exclude("com.google.guava", "guava")
        exclude("com.google.code.findbugs", "jsr305")
        exclude("androidx.legacy", "legacy-support-core-utils")
        exclude("androidx.loader", "loader")
        exclude("androidx.privacysandbox.ads", "ads-adservices-java")
        exclude("androidx.privacysandbox.ads", "ads-adservices")
    }
}

dependencyGuard {
    // All dependencies included in Production Release APK
    configuration("releaseRuntimeClasspath") {
        tree = true
    }
}
