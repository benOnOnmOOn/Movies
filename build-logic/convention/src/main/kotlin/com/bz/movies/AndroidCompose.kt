package com.bz.movies

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(commonExtension: ApplicationExtension) {
    commonExtension.buildFeatures { compose = true }
}

internal fun Project.configureAndroidCompose(commonExtension: LibraryExtension) {
    commonExtension.buildFeatures { compose = true }
}
