plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.dependency.analysis)
    id("com.android.lint")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
