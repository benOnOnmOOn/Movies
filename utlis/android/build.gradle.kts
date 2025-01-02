plugins {
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.movies.binary.compatibility)
}

android {
    namespace = "com.bz.utils.android"
}

dependencyAnalysis {
    issues {
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-android-debug") }
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-core-android-debug") }
    }
}

dependencies {
    implementation(libs.kermit)
    implementation(libs.kermit.core)

    lintChecks(libs.slack.lint.checks)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
