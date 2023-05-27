plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencyAnalysis {
    issues {
        onUnusedDependencies {
            // fix weird issues raised by plugin on kotlin module without any dependency
            exclude("() -> java.io.File?")
        }
    }
}
