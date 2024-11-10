plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

android {
    namespace = "com.bz.movies.datastore"
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

kover {
    currentProject {
        createVariant("custom") {
            add("debug")
        }
    }
}

dependencies {
    implementation(project(":utlis:android"))

    lintChecks(libs.slack.lint.checks)

    // HILT
    implementation(libs.hilt.core)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    implementation(libs.timber)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    api("androidx.datastore:datastore-core:1.1.1")
    implementation("androidx.datastore:datastore:1.1.1")
    implementation(libs.kotlinx.coroutines.core)

    testRuntimeOnly(libs.junit.engine)

    testImplementation(libs.junit.api)

    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
}
