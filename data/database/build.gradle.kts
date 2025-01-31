plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.android.room)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.strict.dependencies)
}

dependencies {

    api(project(":data:dto"))

    implementation(project(":utlis:android"))

    lintChecks(libs.lint.slack.checks)

    // HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    //

    implementation(libs.androidx.room.common)
    implementation(libs.androidx.sqlite)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    testRuntimeOnly(libs.junit.engine)
}
