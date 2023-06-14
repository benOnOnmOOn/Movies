plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.dagger.hilt.android)
    kotlin("kapt")
}

android {
    namespace = "com.bz.presentation.screens"

    kotlinOptions {
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
}


dependencies {
    api(project(":data:network"))
    api(project(":data:database"))
    api(project(":data:dto"))

    releaseImplementation(platform(libs.firebase.bom))

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)

    //  HILT
    kapt(libs.hilt.android.compiler)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.core)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    implementation(libs.androidx.navigation.compose)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)

    api(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    api(libs.androidx.material3)

    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    api(libs.androidx.runtime)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.material3)
    runtimeOnly(libs.androidx.startup.runtime)
    implementation(libs.coil.compose)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.timber)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestRuntimeOnly(libs.junit.engine)
    androidTestImplementation(libs.junit.api)


}
