plugins {
    alias(libs.plugins.binary.compatibility)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.dexcount)
    alias(libs.plugins.movies.android.application)
    alias(libs.plugins.movies.android.application.compose)
    alias(libs.plugins.movies.android.application.firebase)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
}

android {
    namespace = "com.bz.movies"
}

dependencyAnalysis {
    issues {
        onUnusedDependencies { exclude("com.squareup.leakcanary:leakcanary-android") }
        onUnusedDependencies { exclude(libs.androidx.appcompat) }
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-android-debug") }
        onUsedTransitiveDependencies { exclude("co.touchlab:kermit-core-android-debug") }
    }
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
    // don't warn
    implementation(project(":presentation:core"))
    implementation(project(":data:cronet"))
    implementation(project(":utlis:android"))
    kover(project(":presentation:core"))
    kover(project(":presentation:screens"))
    kover(project(":data:network"))
    kover(project(":data:database"))
    kover(project(":data:datastore"))

    lintChecks(libs.slack.lint.checks)

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)
    releaseImplementation(libs.firebase.perf)

    //  HILT
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    implementation(libs.javax.inject)
    //

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.appcompat)
    implementation(libs.dagger)

    implementation(libs.kermit)
    releaseImplementation(libs.kermit.crashlytics)

    implementation(libs.kotlin.stdlib)
    debugImplementation(libs.leakcanary.android)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    debugImplementation(libs.logging.interceptor)

    releaseImplementation(libs.guava)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    releaseImplementation(libs.kermit.core)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
}

dependencyGuard {
    // All dependencies included in Production Release APK
    configuration("releaseRuntimeClasspath")
}
