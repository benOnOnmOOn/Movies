plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.strict.dependencies)
}

dependencies {
    implementation(projects.utlis.android)

    lintChecks(libs.lint.slack.checks)

    ksp(libs.moshi.kotlin.codegen)

    // HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    implementation(libs.javax.inject)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    //

    // CRONET
    api(libs.cronet.okhttp)
    api(libs.cronet.api)
    runtimeOnly(libs.cronet.embedded)
    implementation(libs.play.services.cronet)
    //

    api(libs.okhttp)

    runtimeOnly(libs.kotlinx.coroutines.android)
}
