plugins {
    kotlin("jvm")
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    id("com.android.lint")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

koverReport {
    androidReports("debug") {
        html {
            onCheck = true
        }
        verify { }
    }
}

dependencyAnalysis {
    issues {
        onUnusedDependencies {
            // fix weird issues raised by plugin on kotlin module without any dependency
            exclude("() -> java.io.File?")
        }
    }
}
