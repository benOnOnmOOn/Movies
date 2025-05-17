plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.strict.dependencies)
}

dependencies {
    api(projects.data.dto)
    implementation(projects.utlis.android)
    ksp(libs.moshi.kotlin.codegen)

    lintChecks(libs.lint.slack.checks)

    // HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    implementation(libs.javax.inject)
    //

    implementation(libs.moshi)

    implementation(libs.moshi.converter)
    api(libs.okhttp)
    api(libs.retrofit)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)

    runtimeOnly(libs.kotlinx.coroutines.android)

    testRuntimeOnly(libs.junit.engine)
    //noinspection UseTomlInstead
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(libs.mockk.dsl)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.junit.api)
}
