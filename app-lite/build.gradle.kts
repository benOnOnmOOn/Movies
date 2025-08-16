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
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    implementation(libs.javax.inject)
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
