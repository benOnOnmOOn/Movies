
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
// import org.gradle.android.AndroidCacheFixPlugin
// import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.android.cache.fix) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.binary.compatibility) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dependency.guard) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.kover) apply false
    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.dependency.analysis) apply false
    alias(libs.plugins.movies.dependency.analysis) apply true
    alias(libs.plugins.detekt) apply true
//    alias(libs.plugins.gradle.doctor) apply true
    // Versions plugin need to be enabled here for proper work in whole project
    alias(libs.plugins.gradle.versions) apply true
    alias(libs.plugins.ktlint) apply true
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
}

tasks.register<Detekt>("detektAll") {
    description = "Runs Detekt for all modules"
    baseline = file(baselineFile)
    config.setFrom(files(configFile))
    setSource(projectSource)
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles)
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

tasks.register<DetektCreateBaselineTask>("detektGenerateBaseline") {
    description = "Custom DETEKT build to build baseline for all modules"
    baseline = file(baselineFile)
    config.setFrom(files(configFile))
    setSource(projectSource)
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles)
}

//endregion

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)

        freeCompilerArgs.addAll(listOf("-Xjvm-default=all", "-Xexpect-actual-classes"))
        allWarningsAsErrors.set(true)
        extraWarnings.set(true)
    }
}


ktlint {
    version.set("1.4.0")
}

subprojects {
    apply<KtlintPlugin>()
//    apply<AndroidCacheFixPlugin>()
}

// doctor {
//    daggerThreshold.set(100)
//    negativeAvoidanceThreshold.set(50)
// }
