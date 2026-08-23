plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.strict.dependencies)
}

dependencies {
    implementation(projects.utlis.android)

    lintChecks(libs.lint.slack.checks)

    ksp(libs.moshi.kotlin.codegen)

    //region HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    //endregion

    //region CRONET
    api("org.chromium.net:cronet:500.0.1")
    api(libs.cronet.okhttp)
    runtimeOnly(libs.cronet.bundled)
    implementation(libs.play.services.cronet)
    //endregion

    api(libs.okhttp)

    implementation(libs.kotlin.stdlib)

    runtimeOnly(libs.kotlinx.coroutines.android)
}
