plugins {
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.movies.binary.compatibility)
}

android {
    namespace = "com.bz.cronet"
}

dependencies {
    implementation(project(":utlis:android"))

    lintChecks(libs.slack.lint.checks)

    ksp(libs.moshi.kotlin.codegen)

    // HILT
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    debugApi(libs.javax.inject)
    //

    api(libs.okhttp)

    // use debug impl to prevent from adding this deps to release version
    debugApi(libs.logging.interceptor)

    runtimeOnly(libs.kotlinx.coroutines.android)
}
