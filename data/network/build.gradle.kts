plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
}

android {
    namespace = "com.bz.network"
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

    api(libs.retrofit)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)

    runtimeOnly(libs.kotlinx.coroutines.android)

    testRuntimeOnly(libs.junit.engine)

    testImplementation(libs.converter.moshi)
    testImplementation(libs.mockk.dsl)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.junit.api)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
}
