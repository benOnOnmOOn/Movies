plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

android {
    namespace = "com.bz.presentation.screens"

    kotlinOptions {
        freeCompilerArgs +=
            listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
    }
}

kover {
    currentProject {
        createVariant("custom") {
            add("debug")
        }
    }
}

apiValidation {
    ignoredPackages.add("hilt_aggregated_deps")
    nonPublicMarkers.addAll(
        listOf(
            "dagger.internal.DaggerGenerated",
            "javax.annotation.processing.Generated",
            "dagger.hilt.codegen.OriginatingElement"
        )
    )
}

dependencyAnalysis {
    issues {
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-android-debug") }
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-core-android-debug") }
    }
}

dependencies {
    api(project(":data:network"))
    api(project(":data:database"))
    api(project(":data:datastore"))
    implementation(project(":data:dto"))

    lintChecks(libs.slack.lint.checks)
    lintChecks(libs.compose.lint.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    api(libs.androidx.animation.android)
    api(libs.androidx.runtime.android)
    api(libs.androidx.ui.android)
    api(libs.androidx.navigation.runtime)
    api(libs.androidx.navigation.common)
    api(libs.androidx.material3)
    api(libs.androidx.lifecycle.common)
    api(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.androidx.ui.unit.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.collection.jvm)
    debugImplementation(libs.androidx.ui.tooling.preview.android)

    api(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.base)
    runtimeOnly(libs.androidx.startup.runtime)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.kermit)
    releaseImplementation(libs.kermit.core)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    debugImplementation(libs.leakcanary.watcher.android.core)
    debugImplementation(libs.leakcanary.watcher)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.dsl)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testRuntimeOnly(libs.junit.engine)

    testImplementation(libs.kermit.core)

    androidTestImplementation(libs.androidx.monitor)
    androidTestRuntimeOnly(libs.junit.engine)
    androidTestImplementation(libs.junit.api)
}
