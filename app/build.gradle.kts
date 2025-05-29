plugins {
    alias(libs.plugins.dexcount)
    alias(libs.plugins.movies.android.application)
    alias(libs.plugins.movies.android.application.compose)
    alias(libs.plugins.movies.android.application.firebase)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.strict.dependencies)
    alias(libs.plugins.movies.dependency.analysis)
}

android {
    namespace = "com.bz.movies"
}

dependencies {
    // don't warn
    implementation(projects.presentation.core)
    implementation(projects.utlis.android)
    debugImplementation(projects.data.okhttp)
    releaseImplementation(projects.data.cronet)
    val enableKover =
        providers.gradleProperty("movies.enableKover").getOrElse("true").toBoolean()
    if (enableKover) {
        add("kover", projects.presentation.core)
        add("kover", projects.presentation.screens)
        add("kover", projects.data.database)
        add("kover", projects.data.network)
        add("kover", projects.data.datastore)
    }

    lintChecks(libs.lint.slack.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    implementation(libs.javax.inject)
    //

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)
    releaseImplementation(libs.firebase.perf)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.appcompat)
    implementation(libs.dagger)

    implementation(libs.kermit)
    releaseImplementation(libs.kermit.crashlytics)

    implementation(libs.kotlin.stdlib)
    debugRuntimeOnly(libs.leakcanary.android)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    debugImplementation(libs.logging.interceptor)

    releaseImplementation(libs.guava)

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
