plugins {
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

android {
    namespace = "com.bz.utils.android"
}

apiValidation {
    ignoredPackages.add("hilt_aggregated_deps")
    nonPublicMarkers.addAll(
        listOf(
            "dagger.internal.DaggerGenerated",
            "javax.annotation.processing.Generated",
            "dagger.hilt.codegen.OriginatingElement"
        )
    )
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
