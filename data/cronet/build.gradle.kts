plugins {
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
}

dependencies {
    implementation(project(":utlis:android"))

    lintChecks(libs.slack.lint.checks)

    ksp(libs.moshi.kotlin.codegen)

    // HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    //

    // CRONET
    releaseApi(libs.cronet.okhttp)
    releaseApi(libs.cronet.api)
    releaseRuntimeOnly(libs.cronet.embedded)
    releaseImplementation(libs.play.services.cronet)
    //

    api(libs.okhttp)

    // use debug impl to prevent from adding this deps to release version
    debugApi(libs.logging.interceptor)

    runtimeOnly(libs.kotlinx.coroutines.android)
}
