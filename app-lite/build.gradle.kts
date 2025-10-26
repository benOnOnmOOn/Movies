import com.autonomousapps.DependencyAnalysisSubExtension
import kotlin.apply
import org.gradle.kotlin.dsl.android
import org.gradle.kotlin.dsl.findByType

plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.dexcount)
    alias(libs.plugins.movies.android.application)
    alias(libs.plugins.movies.android.application.compose)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.ktlint)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.strict.dependencies)
}

android {
    namespace = "com.bz.movies.lite"
    buildTypes {
        release {
            optimization {
                keepRules {
                    ignoreFrom(
                        "androidx.compose.animation:animation-android",
                        "androidx.compose.animation:animation-core-android",
                        "androidx.compose.animation:animation-core",
                        "androidx.compose.animation:animation",
                        "androidx.compose.foundation:foundation-android",
                        "androidx.compose.foundation:foundation-layout-android",
                        "androidx.compose.foundation:foundation-layout",
                        "androidx.compose.foundation:foundation",
                        "androidx.compose.material3:material3-android",
                        "androidx.compose.material3:material3",
                        "androidx.compose.material:material-ripple",
                        "androidx.compose.material:material",
                        "androidx.compose.runtime:runtime-android",
                        "androidx.compose.runtime:runtime-annotation-android",
                        "androidx.compose.runtime:runtime-annotation",
                        "androidx.compose.runtime:runtime-saveable-android",
                        "androidx.compose.runtime:runtime-saveable",
                        "androidx.compose.runtime:runtime",
                        "androidx.compose.ui:ui-android",
                        "androidx.compose.ui:ui-geometry-android",
                        "androidx.compose.ui:ui-geometry",
                        "androidx.compose.ui:ui-graphics-android",
                        "androidx.compose.ui:ui-graphics",
                        "androidx.compose.ui:ui-text-android",
                        "androidx.compose.ui:ui-text",
                        "androidx.compose.ui:ui-unit-android",
                        "androidx.compose.ui:ui-unit",
                        "androidx.compose.ui:ui-util-android",
                        "androidx.compose.ui:ui-util",
                        "androidx.compose.ui:ui"
                    )
                }
            }
            proguardFiles("proguard-rules-compose.pro")
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

    implementation(projects.data.database)
    implementation(projects.data.datastore)
    implementation(projects.data.network)
    implementation(projects.data.okhttp)
    implementation(projects.presentation.coreLite)
    implementation(projects.presentation.screens)
    implementation(projects.utlis.android)

    runtimeOnly(projects.utlis.leakstub)

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
    ksp(libs.kotlin.metadata)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    compileOnly(libs.javax.inject)
    //

    implementation(libs.androidx.startup.runtime)
    compileOnly(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.datastore.core)

    implementation(libs.dagger)
    implementation(libs.kermit)
    implementation(libs.kotlin.stdlib)

    implementation(libs.okhttp)
    implementation(libs.okhttp.android)
    implementation(libs.retrofit)
    debugImplementation(libs.logging.interceptor)

    releaseImplementation(libs.kermit.core)

    debugRuntimeOnly(libs.androidx.compose.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.compose.ui.tooling)

    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)
}

dependencyGuard {
    // All dependencies included in Production Release APK
    configuration("releaseRuntimeClasspath")
}

configurations.configureEach {
    exclude("androidx.vectordrawable", "vectordrawable")
    exclude("androidx.vectordrawable", "vectordrawable-animated")
    exclude("androidx.versionedparcelable", "versionedparcelable")
    exclude("org.jetbrains.kotlinx", "kotlinx-serialization-bom")
    exclude("org.jetbrains.kotlinx", "kotlinx-serialization-core-jvm")
    exclude("org.jetbrains.kotlinx", "kotlinx-serialization-core")
}

configurations.runtimeOnly {
    exclude("androidx.fragment", "fragment")
}
