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

kotlin {
    compilerOptions {
        optIn = listOf(
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}

dependencies {
    api(projects.data.network)
    api(projects.data.database)
    api(projects.data.datastore)
    implementation(projects.data.dto)

    compileOnly(projects.utlis.leakstub)

    lintChecks(libs.lint.slack.checks)
    lintChecks(libs.lint.compose.checks)

    //region HILT
    ksp(libs.hilt.android.compiler)
    ksp(libs.dagger.compiler)
    api(libs.dagger)
    implementation(libs.javax.inject)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    //

    api(libs.androidx.compose.animation.android)
    api(libs.androidx.compose.foundation.layout.android)
    api(libs.androidx.compose.runtime.android)
    api(libs.androidx.compose.ui.android)
    api(libs.androidx.lifecycle.common)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.material3)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)

    implementation(libs.androidx.collection)
    implementation(libs.androidx.collection.jvm)
    implementation(libs.androidx.compose.foundation.android)
    implementation(libs.androidx.compose.runtime.annotation)
    implementation(libs.androidx.compose.ui.graphics.android)
    implementation(libs.androidx.compose.ui.text.android)
    implementation(libs.androidx.compose.ui.unit.android)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kermit)
    implementation(libs.kotlinx.coroutines.core)

    runtimeOnly(libs.androidx.startup.runtime)

    debugImplementation(libs.androidx.compose.ui.tooling.preview.android)

    debugRuntimeOnly(libs.androidx.compose.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.compose.ui.tooling)

    releaseImplementation(libs.kermit.core)

    testImplementation(libs.junit.api)
    testImplementation(libs.kermit.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.dsl)
    testImplementation(libs.turbine)
    testRuntimeOnly(libs.junit.engine)
    //noinspection UseTomlInstead
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
