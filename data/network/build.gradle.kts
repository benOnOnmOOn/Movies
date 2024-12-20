plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

android {
    namespace = "com.bz.network"
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
        onUsedTransitiveDependencies { exclude(libs.okhttp) }
    }
}

kover {
    currentProject {
        createVariant("custom") {
            add("debug")
        }
    }
}

dependencies {
    api(project(":data:dto"))
    implementation(project(":utlis:android"))
    ksp(libs.moshi.kotlin.codegen)

    lintChecks(libs.slack.lint.checks)

    // HILT
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    implementation(libs.moshi)

    implementation(libs.converter.moshi)
    api(libs.okhttp)
    api(libs.retrofit)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)

    runtimeOnly(libs.kotlinx.coroutines.android)

    testRuntimeOnly(libs.junit.engine)

    testImplementation(libs.mockk.dsl)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.junit.api)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
}
