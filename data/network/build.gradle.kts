plugins {
    embeddedKotlin("android")
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
}

android {
    namespace = "com.bz.network"
}

dependencies {
    api(project(":data:dto"))

    ksp(libs.moshi.kotlin.codegen)

    // HILT
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    implementation(libs.moshi)
    implementation(libs.converter.moshi)
    api(libs.okhttp)
    api(libs.retrofit)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)

    // use debug impl to prevent from adding this deps to release version
    debugApi(libs.logging.interceptor)

    testRuntimeOnly(libs.junit.engine)

    testImplementation(libs.mockk.dsl)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.junit.api)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)

    runtimeOnly(libs.kotlinx.coroutines.android)
}
