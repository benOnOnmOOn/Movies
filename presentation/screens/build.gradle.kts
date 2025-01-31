plugins {
    alias(libs.plugins.movies.dependency.analysis)
    alias(libs.plugins.movies.android.library)
    alias(libs.plugins.movies.android.library.compose)
    alias(libs.plugins.movies.binary.compatibility)
    alias(libs.plugins.movies.hilt)
    alias(libs.plugins.movies.strict.dependencies)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.movies.ktlint)
}

android {
    kotlinOptions {
        freeCompilerArgs +=
            listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
    }
}

dependencies {
    api(project(":data:network"))
    api(project(":data:database"))
    api(project(":data:datastore"))
    implementation(project(":data:dto"))

    lintChecks(libs.lint.slack.checks)
    lintChecks(libs.lint.compose.checks)

    //region HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    api(libs.javax.inject)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    //

    api(libs.androidx.animation.android)
    api(libs.androidx.foundation.layout.android)
    api(libs.androidx.lifecycle.common)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.material3)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.androidx.runtime.android)
    api(libs.androidx.ui.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.collection.jvm)
    implementation(libs.androidx.core)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.androidx.ui.unit.android)
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.base)
    implementation(libs.kermit)
    implementation(libs.kotlinx.coroutines.core)

    runtimeOnly(libs.androidx.startup.runtime)

    debugImplementation(libs.androidx.ui.tooling.preview.android)
    debugImplementation(libs.leakcanary.watcher)
    debugImplementation(libs.leakcanary.watcher.android.core)
    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    releaseImplementation(libs.kermit.core)

    testImplementation(libs.junit.api)
    testImplementation(libs.kermit.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.dsl)
    testImplementation(libs.turbine)
    testRuntimeOnly(libs.junit.engine)
}
