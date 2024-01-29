plugins {
    embeddedKotlin("jvm")
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    id("com.android.lint")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

