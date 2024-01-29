import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
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
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    embeddedKotlin("android") apply false
    embeddedKotlin("jvm") apply true
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

//region Global kotlin and java configuration
allprojects {
    tasks.withType<KotlinCompile>().configureEach { kotlinOptions.jvmTarget = "17" }
}
kotlin {
    jvmToolchain(17)
}

//endregion

dependencyAnalysis {
    issues {
        all { onAny { severity("fail") } }
        all { onUnusedDependencies { exclude("org.jetbrains.kotlin:kotlin-stdlib") } }
    }

    structure {
        bundle("kotlin-stdlib") {
            includeGroup("org.jetbrains.kotlin")
        }
    }
}

fun PluginContainer.applyBaseConfig(project: Project) {
    whenPluginAdded {
        when (this) {
            is AppPlugin ->
                project.extensions.getByType<BaseAppModuleExtension>().baseConfig()

            is LibraryPlugin ->
                project.extensions.getByType<LibraryExtension>().baseConfig()

            is KoverGradlePlugin ->
                project.extensions.getByType<KoverReportExtension>().baseConfig()

            is HiltGradlePlugin ->
                project.extensions.getByType<HiltExtension>().baseConfig()

            is KtlintPlugin -> {
                project.extensions.getByType<KtlintExtension>().baseConfig()
            }
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
    AR : AndroidResources
    > CommonExtension<BF, BT, DC, PF, AR>.defaultBaseConfig() {
    compileSdk = libs.versions.android.sdk.target.get().toInt()
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = true
        checkAllWarnings = true
        warningsAsErrors = true
        checkReleaseBuilds = false
        checkDependencies = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
            "**.properties",
            "kotlin-tooling-metadata.json"
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

fun KoverReportExtension.baseConfig() {
    defaults {
        html {
            onCheck = true
        }
        xml {
            onCheck = true
        }
    }
}

fun HiltExtension.baseConfig() {
    enableAggregatingTask = true
}

ktlint {
    version.set("1.1.1")
}

fun KtlintExtension.baseConfig() {
    version.set("1.1.1")
    filter {
        exclude("**/generated/**")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.gradle.android.cache-fix")
}

doctor {
    daggerThreshold.set(100)
    negativeAvoidanceThreshold.set(50)
}
