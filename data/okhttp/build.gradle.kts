plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.strict.dependencies)
    alias(libs.plugins.movies.ktlint)
}

dependencies {
    implementation(project(":utlis:android"))

    lintChecks(libs.lint.slack.checks)

    ksp(libs.moshi.kotlin.codegen)

    // HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    api(libs.dagger)
    debugImplementation(libs.javax.inject)
    //

    api(libs.okhttp)

    // use debug impl to prevent from adding this deps to release version
    debugApi(libs.logging.interceptor)

    runtimeOnly(libs.kotlinx.coroutines.android)
}
