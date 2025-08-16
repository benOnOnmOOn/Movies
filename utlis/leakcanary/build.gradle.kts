plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.strict.dependencies)
    alias(libs.plugins.movies.ktlint)
}

dependencies {
    implementation(libs.kermit)
    implementation(libs.kermit.core)

    implementation(libs.leakcanary.watcher)
    implementation(libs.leakcanary.watcher.android.core)

    api(libs.androidx.lifecycle.viewmodel)

    lintChecks(libs.lint.slack.checks)
}
