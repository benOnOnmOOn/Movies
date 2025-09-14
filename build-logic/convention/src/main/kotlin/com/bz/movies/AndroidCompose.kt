package com.bz.movies

import com.android.build.api.dsl.CommonExtension

/**
 * Configure Compose-specific options
 */
internal fun CommonExtension.configureAndroidCompose() {
    buildFeatures.compose = true
}
