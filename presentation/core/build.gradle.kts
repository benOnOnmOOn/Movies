plugins {
    embeddedKotlin("android")
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bz.core"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":presentation:screens"))

    releaseImplementation(platform(libs.firebase.bom))

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)

    //  HILT
    ksp(libs.hilt.android.compiler)
    api(libs.hilt.android)
    api(libs.hilt.core)
    //

    api(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.annotation)

    api(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.runtime)

    api(libs.androidx.activity)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
}
