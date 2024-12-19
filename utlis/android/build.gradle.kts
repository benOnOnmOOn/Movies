plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.com.android.library)
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

dependencies {
    api(libs.javax.inject)

    lintChecks(libs.slack.lint.checks)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
