@file:Suppress("UnstableApiUsage")

plugins {
    embeddedKotlin("android")
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dependency.guard)
    alias(libs.plugins.dexcount)
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
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")

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
        onUnusedDependencies { exclude(libs.androidx.appcompat) }
        onIncorrectConfiguration { exclude("org.jetbrains.kotlin:kotlin-stdlib") }
    }
}

dependencies {
    // don't warn
    implementation(project(":presentation:core"))
    implementation(project(":data:okhttp"))
    kover(project(":presentation:core"))
    kover(project(":presentation:screens"))
    kover(project(":data:network"))
    kover(project(":data:database"))

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
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
    implementation(libs.retrofit)
    debugImplementation(libs.logging.interceptor)
}

configurations {
    implementation {
        exclude("com.google.code.findbugs", "jsr305")
        exclude("com.google.errorprone", "error_prone_annotations")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("androidx.loader", "loader")
        exclude("androidx.cursoradapter", "cursoradapter")
        exclude("androidx.customview", "customview")
        exclude("androidx.viewpager", "viewpager")
        exclude("androidx.versionedparcelable", "versionedparcelable")
        exclude("androidx.vectordrawable", "vectordrawable-animated")
        exclude("androidx.vectordrawable", "vectordrawable")
        exclude("androidx.drawerlayout", "drawerlayout")
        exclude("org.checkerframework", "checker-qual")
    }
}

dependencyGuard {
    // All dependencies included in Production Release APK
    configuration("releaseRuntimeClasspath") {
        tree = true
    }
}
