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
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-android-debug") }
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-core-android-debug") }
        onUnusedDependencies { exclude("com.google.dagger:hilt-android") }
    }
}

dependencies {
    implementation(project(":utlis:android"))

    lintChecks(libs.slack.lint.checks)

    // HILT
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    api(libs.androidx.datastore.core)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.kermit)
    implementation(libs.kotlinx.coroutines.core)

    releaseImplementation(libs.kermit.core)

    testRuntimeOnly(libs.junit.engine)
}
