plugins {
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

android {
    namespace = "com.bz.cronet"
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
    implementation(project(":utlis:android"))

    lintChecks(libs.slack.lint.checks)

    ksp(libs.moshi.kotlin.codegen)

    // HILT
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    //

    // CRONET
    releaseApi(libs.cronet.okhttp)
    releaseApi(libs.cronet.api)
    releaseRuntimeOnly(libs.cronet.embedded)
    releaseImplementation(libs.play.services.cronet)
    //

    api(libs.okhttp)

    // use debug impl to prevent from adding this deps to release version
    debugApi(libs.logging.interceptor)

    runtimeOnly(libs.kotlinx.coroutines.android)
}
