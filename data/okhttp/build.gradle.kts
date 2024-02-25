plugins {
    embeddedKotlin("android")
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
}

android {
    namespace = "com.bz.cronet"
}

dependencies {
    api(project(":data:dto"))
    implementation(project(":utlis:android"))
    implementation(project(":data:network"))

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

    runtimeOnly(libs.kotlinx.coroutines.android)
}
