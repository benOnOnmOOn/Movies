import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.Installation
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import dagger.hilt.android.plugin.HiltExtension
import dagger.hilt.android.plugin.HiltGradlePlugin
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.gradle.versions) apply true
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.dependency.analysis) apply true
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.org.jetbrains.kotlinx.kover) apply false
    alias(libs.plugins.com.osacky.doctor) apply true
    alias(libs.plugins.org.jlleitschuh.gradle.ktlint) apply true
    alias(libs.plugins.org.gradle.android.cache.fix) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.compose.compiler) apply false
}

//region Dependency Updates Task

fun isNonStable(version: String): Boolean {
    val unStableKeyword =
        listOf("ALPHA", "BETA").any {
            version.contains(it, ignoreCase = true)
        }
    if (unStableKeyword) return true

    val stableKeyword =
        listOf("RELEASE", "FINAL", "GA").any {
            version.contains(it, ignoreCase = true)
        }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

//endregion

//region Detekt

val projectSource = file(projectDir)
val configFile = files("$rootDir/config/detekt/detekt.yml")
val baselineFile = file("$rootDir/config/detekt/baseline.xml")
val kotlinFiles = "**/*.kt"
val resourceFiles = "**/resources/**"
val buildFiles = "**/build/**"

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files(configFile))
    baseline = file(baselineFile)
    parallel = true
    ignoreFailures = false
    autoCorrect = false
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

tasks.register<Detekt>("detektAll") {
    description = "Runs Detekt for all modules"
    allRules = false
    config = files(configFile)
    baseline = file(baselineFile)
    setSource(projectSource)
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles)
}

tasks.register<DetektCreateBaselineTask>("detektGenerateBaseline") {
    description = "Custom DETEKT build to build baseline for all modules"
    setSource(projectSource)
    baseline.set(baselineFile)
    config.setFrom(configFile)
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles)
}

//endregion

dependencyAnalysis {
    issues {
        all { onAny { severity("fail") } }
    }
}

fun PluginContainer.applyBaseConfig(project: Project) {
    whenPluginAdded {
        when (this) {
            is AppPlugin ->
                project.extensions.getByType<BaseAppModuleExtension>().baseConfig()

            is LibraryPlugin ->
                project.extensions.getByType<LibraryExtension>().baseConfig()

            is HiltGradlePlugin ->
                project.extensions.getByType<HiltExtension>().baseConfig()
        }
    }
}

//region Global android configuration
@Suppress("UnstableApiUsage")
fun <
    BF : BuildFeatures,
    BT : BuildType,
    DC : DefaultConfig,
    PF : ProductFlavor,
    AR : AndroidResources,
    IN : Installation
    > CommonExtension<BF, BT, DC, PF, AR, IN>.defaultBaseConfig() {
    compileSdk = libs.versions.android.sdk.target.get().toInt()
    buildToolsVersion = "35.0.0"

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        baseline = project.file("lint-baseline.xml")
        disable += listOf("NewerVersionAvailable", "GradleDependency", "RawDispatchersUse")
        abortOnError = true
        checkAllWarnings = true
        warningsAsErrors = true
        checkReleaseBuilds = false
        checkDependencies = false
        checkGeneratedSources = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.compose.compiler.extension.get()
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    packaging.resources.excludes +=
        setOf(
            "kotlin/**",
            "META-INF/**",
            "META-INF/services/**",
            "**.properties",
            "kotlin-tooling-metadata.json",
            "DebugProbesKt.bin"
        )
}

fun LibraryExtension.baseConfig() {
    defaultBaseConfig()
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

fun BaseAppModuleExtension.baseConfig() {
    defaultBaseConfig()
    dependenciesInfo.apply {
        includeInApk = false
        includeInBundle = false
    }
    defaultConfig {
        targetSdk = libs.versions.android.sdk.target.get().toInt()
        multiDexEnabled = false
    }
    compileOptions.isCoreLibraryDesugaringEnabled = false
    compileOptions.incremental = true
}

subprojects {
    project.plugins.applyBaseConfig(project)
}
// endregion

fun HiltExtension.baseConfig() {
    enableAggregatingTask = true
}

ktlint {
    version.set("1.3.1")
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.gradle.android.cache-fix")
    configurations.all {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("com.google.code.findbugs", "jsr305")
        exclude("com.google.errorprone", "error_prone_annotations")
        exclude("androidx.legacy", "legacy-support-core-utils")
        exclude("androidx.loader", "loader")
        exclude("androidx.privacysandbox.ads", "ads-adservices-java")
        exclude("androidx.privacysandbox.ads", "ads-adservices")
        exclude("androidx.cursoradapter", "cursoradapter")
        exclude("androidx.customview", "customview")
        exclude("androidx.versionedparcelable", "versionedparcelable")
        exclude("androidx.vectordrawable", "vectordrawable-animated")
        exclude("androidx.vectordrawable", "vectordrawable")
        exclude("androidx.drawerlayout", "drawerlayout")
        exclude("org.checkerframework", "checker-qual")
        exclude("androidx.viewpager", "viewpager")
    }
}

doctor {
    daggerThreshold.set(100)
    negativeAvoidanceThreshold.set(50)
}
