plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.junit5)
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.firebase.crashlytics.gradle) apply false
    kotlin("kapt")
    alias(libs.plugins.com.google.dagger.hilt.android)
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
        freeCompilerArgs = listOf(
            "-Xjvm-default=all",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = true
        checkAllWarnings = true
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

dependencies {
    implementation(project(":data:network"))

    releaseImplementation(platform(libs.firebase.bom))

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)

    //  HILT
    kapt(libs.hilt.android.compiler)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.core)
    implementation(libs.dagger)
    implementation(libs.javax.inject)
    //

    implementation(libs.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.material3)

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)



}
