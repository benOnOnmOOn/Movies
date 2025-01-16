plugins {
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.dependencies)
}

dependencyAnalysis {
    issues {
        onUsedTransitiveDependencies { exclude(libs.okhttp) }
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
}
