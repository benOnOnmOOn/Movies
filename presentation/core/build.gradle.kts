plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.android.library.compose)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.strict.dependencies)
    alias(libs.plugins.movies.ktlint)
}

dependencies {
    api(projects.presentation.screens)

    lintChecks(libs.lint.slack.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    api(libs.dagger)

    //

    implementation(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.compose.runtime.android)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    compileOnly(libs.androidx.annotation)
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.ui.android)

    debugRuntimeOnly(libs.androidx.compose.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.compose.ui.tooling)

    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)
}
