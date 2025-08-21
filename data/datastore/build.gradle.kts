import com.autonomousapps.DependencyAnalysisSubExtension

plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.strict.dependencies)
}

extensions.findByType<DependencyAnalysisSubExtension>()?.apply {
    issues {
        onUnusedDependencies { exclude("com.google.dagger:hilt-android") }
    }
}

dependencies {
    implementation(projects.utlis.android)

    lintChecks(libs.lint.slack.checks)

    // HILT
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    compileOnly(libs.javax.inject)
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
