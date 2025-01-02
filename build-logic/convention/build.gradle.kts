/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.lint)
    alias(libs.plugins.dependency.analysis)
}

group = "com.bz.movies.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
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
    }
}
