plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.com.android.library)
}

android {
    namespace = "com.bz.utils.android"
}

dependencies {
    lintChecks(libs.slack.lint.checks)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
