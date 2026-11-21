import com.autonomousapps.DependencyAnalysisSubExtension

plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.strict.dependencies)
    alias(libs.plugins.movies.ktlint)
}

extensions.findByType<DependencyAnalysisSubExtension>()?.apply {
    issues {
        onUnusedDependencies { exclude("com.google.dagger:hilt-android") }
    }
}

dependencies {
    implementation(projects.utlis.android)

    lintChecks(libs.lint.slack.checks)

    ksp(libs.moshi.kotlin.codegen)

    //region HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    api(libs.dagger)
    //endregion

    api(libs.okhttp)

    // use debug impl to prevent from adding this deps to release version
    debugImplementation(libs.logging.interceptor)

    implementation(libs.kotlin.stdlib)

    runtimeOnly(libs.kotlinx.coroutines.android)
  implementation(libs.javax.inject)
}
