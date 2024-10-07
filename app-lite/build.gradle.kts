plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dependency.guard)
    alias(libs.plugins.dexcount)
    alias(libs.plugins.dependency.analysis)
}

android.namespace = "com.bz.movies"

dependencyAnalysis.issues {
    onUnusedDependencies { exclude(libs.androidx.appcompat) }
}

dependencies {
    // don't warn
    implementation(project(":presentation:core"))
    implementation(project(":data:okhttp"))
    kover(project(":presentation:core"))
    kover(project(":presentation:screens"))
    kover(project(":data:network"))
    kover(project(":data:database"))

    lintChecks(libs.slack.lint.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    //

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.appcompat)
    implementation(libs.dagger)
    implementation(libs.timber)
    implementation(libs.kotlin.stdlib)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    debugImplementation(libs.logging.interceptor)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

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
