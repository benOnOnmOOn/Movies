import com.autonomousapps.DependencyAnalysisSubExtension

plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.strict.dependencies)
}

extensions.findByType<DependencyAnalysisSubExtension>()?.apply {
    issues {
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-android-debug") }
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-core-android-debug") }
    }
}

dependencies {
    implementation(libs.kermit)
    implementation(libs.kermit.core)

    lintChecks(libs.slack.lint.checks)

    testRuntimeOnly(libs.junit.engine)
}
