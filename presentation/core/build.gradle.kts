plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.android.library.compose)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.strict.dependencies)
}

dependencies {
    implementation(project(":presentation:screens"))

    lintChecks(libs.slack.lint.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    api(libs.hilt.android)
    api(libs.hilt.core)
    //

    api(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.runtime.android)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.ui.graphics.android)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)
}
