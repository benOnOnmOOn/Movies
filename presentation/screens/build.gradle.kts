plugins {
    embeddedKotlin("android")
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bz.presentation.screens"

    kotlinOptions {
        freeCompilerArgs +=
            listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":data:network"))
    api(project(":data:database"))
    api(project(":data:dto"))

    //  HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.core)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    api(libs.androidx.animation.android)
    api(libs.androidx.runtime.android)
    api(libs.androidx.ui.android)
    api(libs.androidx.navigation.runtime)
    api(libs.androidx.navigation.common)
    api(libs.androidx.material3)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.material.android)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.ui.unit.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.appcompat)

    api(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.base)
    runtimeOnly(libs.androidx.startup.runtime)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.timber)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.dsl)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    implementation(libs.leakcanary.watcher.android.core)
    implementation(libs.leakcanary.watcher)

    androidTestImplementation(libs.androidx.monitor)
    androidTestRuntimeOnly(libs.junit.engine)
    androidTestImplementation(libs.junit.api)
}
