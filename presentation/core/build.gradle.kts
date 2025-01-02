plugins {
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.android.library.compose)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

android {
    namespace = "com.bz.core"
}

apiValidation {
    ignoredPackages.add("hilt_aggregated_deps")
    nonPublicMarkers.addAll(
        listOf(
            "dagger.internal.DaggerGenerated",
            "javax.annotation.processing.Generated",
            "dagger.hilt.codegen.OriginatingElement"
        )
    )
}

dependencies {
    implementation(project(":presentation:screens"))

    lintChecks(libs.slack.lint.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    api(libs.hilt.android)
    api(libs.hilt.core)
    //

    api(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)
    api(libs.androidx.appcompat)

    api(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.runtime)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
}
