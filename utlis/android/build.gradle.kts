plugins {
    embeddedKotlin("android")
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.com.android.library)
}

android {
    namespace = "com.bz.utils.android"
}

dependencies {
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
