import com.autonomousapps.DependencyAnalysisSubExtension
import kotlin.apply
import org.gradle.kotlin.dsl.findByType

plugins {
    alias(libs.plugins.dexcount)
    alias(libs.plugins.movies.android.application)
    alias(libs.plugins.movies.android.application.compose)
    alias(libs.plugins.movies.android.application.firebase)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.strict.dependencies)
    alias(libs.plugins.movies.dependency.analysis)
}

android {
    namespace = "com.bz.movies"
    buildTypes {
        release {
            optimization {
                keepRules {
                    ignoreFrom(
                        "org.chromium.net:cronet-api",
                        "org.chromium.net:cronet-common",
                        "org.chromium.net:cronet-embedded",
                        "org.chromium.net:cronet-shared"
                    )
                }
            }
        }
    }
}

extensions.findByType<DependencyAnalysisSubExtension>()?.apply {
    issues {
        onUnusedDependencies { exclude(projects.utlis.leakstub) }
        onUnusedDependencies { exclude(projects.utlis.leakcanary) }
    }
}
dependencies {
    // don't warn
    implementation(projects.data.database)
    implementation(projects.data.datastore)
    implementation(projects.data.network)
    implementation(projects.presentation.core)
    implementation(projects.presentation.screens)
    implementation(projects.utlis.android)

    val enableKover =
        providers.gradleProperty("movies.enableKover").getOrElse("true").toBoolean()
    if (enableKover) {
        add("kover", projects.presentation.core)
        add("kover", projects.presentation.screens)
        add("kover", projects.data.database)
        add("kover", projects.data.network)
        add("kover", projects.data.datastore)
    }

    debugRuntimeOnly(projects.utlis.leakcanary)
    releaseRuntimeOnly(projects.utlis.leakstub)

    debugImplementation(projects.data.okhttp)
    releaseImplementation(projects.data.cronet)

    lintChecks(libs.lint.slack.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.kotlin.metadata)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    compileOnly(libs.javax.inject)
    //

    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)
    releaseImplementation(libs.firebase.perf)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.datastore.core)
    implementation(libs.dagger)

    implementation(libs.kermit)
    releaseImplementation(libs.kermit.crashlytics)

    implementation(libs.kotlin.stdlib)

    implementation(libs.okhttp)
    implementation(libs.okhttp.android)
    implementation(libs.retrofit)
    debugImplementation(libs.logging.interceptor)

    releaseImplementation(libs.guava)

    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    releaseImplementation(libs.kermit.core)

    debugRuntimeOnly(libs.androidx.compose.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.compose.ui.tooling)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
}

dependencyGuard {
    // All dependencies included in Production Release APK
    configuration("releaseRuntimeClasspath")
}
