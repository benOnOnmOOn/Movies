plugins {
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.android.room)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
}

dependencies {

    api(project(":data:dto"))

    implementation(project(":utlis:android"))

    lintChecks(libs.slack.lint.checks)

    // HILT
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    implementation(libs.androidx.room.common)
    implementation(libs.androidx.sqlite)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    testRuntimeOnly(libs.junit.engine)

    testImplementation(libs.junit.api)

    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
}
