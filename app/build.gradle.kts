import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.firebase.crashlytics.gradle) apply false
    alias(libs.plugins.com.google.dagger.hilt.android)
    kotlin("kapt")
}

android {
    namespace = "com.bz.movies"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.bz.movies"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations.add("en")
    }

    buildTypes {
        release {
            apply(plugin = "com.google.gms.google-services")
            apply(plugin = "com.google.firebase.crashlytics")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                @Suppress("UnstableApiUsage")
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        languageVersion = KotlinVersion.KOTLIN_2_0.version
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = true
        checkAllWarnings = true
        checkDependencies = true
        warningsAsErrors = true
        checkReleaseBuilds = false
    }

    packagingOptions.resources.excludes += setOf(
        "META-INF/**"
    )
}

kapt {
    correctErrorTypes = true
}

dependencyAnalysis {
    issues { onUnusedDependencies { exclude(":presentation:core") } }
}

dependencies {
    //don't warn
    implementation(project(":presentation:core"))

    releaseImplementation(platform(libs.firebase.bom))

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)

    //  HILT
    kapt(libs.hilt.android.compiler)
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
