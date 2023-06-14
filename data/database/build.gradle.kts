plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    kotlin("kapt")
}

android {
    namespace = "com.bz.movies.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

dependencies {

    api(project(":data:dto"))
    // HILT
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.core)
    kapt(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    implementation(libs.androidx.room.common)
    implementation(libs.androidx.sqlite)
    implementation(libs.kotlinx.coroutines.core)

    api(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit.api)

    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.androidx.monitor)
}
