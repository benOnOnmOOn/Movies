plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.dependency.analysis)
}

android {
    namespace = "com.bz.movies.database"
}

room {
    schemaDirectory("$projectDir/schemas/")
}

ksp {
    arg("room.generateKotlin", "true")
}

kover {
    currentProject {
        createVariant("custom") {
            add("debug")
        }
    }
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
