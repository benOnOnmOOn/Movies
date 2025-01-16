import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.lint)
    alias(libs.plugins.dependency.analysis) apply true
    alias(libs.plugins.ktlint) apply true
}

group = "com.bz.movies.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

ktlint {
    version.set("1.4.0")
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
    compileOnly(libs.binary.compatibility.gradlePlugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.firebase.performance.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    implementation(libs.truth)
    lintChecks(libs.androidx.lint.gradle)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = libs.plugins.movies.android.application.compose.get().pluginId
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.movies.android.application.asProvider().get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.movies.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.movies.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidTest") {
            id = libs.plugins.movies.android.test.get().pluginId
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.movies.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.movies.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidFirebase") {
            id = libs.plugins.movies.android.application.firebase.get().pluginId
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
        register("androidLint") {
            id = libs.plugins.movies.android.lint.get().pluginId
            implementationClass = "AndroidLintConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.movies.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("kover") {
            id = libs.plugins.movies.kover.get().pluginId
            implementationClass = "KoverConventionPlugin"
        }
        register("binaryCompatibility") {
            id = libs.plugins.movies.binary.compatibility.get().pluginId
            implementationClass = "BinaryCompatibilityConventionPlugin"
        }
        register("dependenciesConventionPlugin") {
            id = libs.plugins.movies.dependencies.get().pluginId
            implementationClass = "DependenciesConventionPlugin"
        }
    }
}
