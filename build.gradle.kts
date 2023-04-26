import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.gradle.versions) apply true
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependency.analysis) apply true
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any {
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

val projectSource = file(projectDir)
val configFile = files("$rootDir/config/detekt/detekt.yml")
val baselineFile = file("$rootDir/config/detekt/baseline.xml")
val kotlinFiles = "**/*.kt"
val resourceFiles = "**/resources/**"
val buildFiles = "**/build/**"
val testFiles = "**/test/**"
val androidTestFiles = "**/androidTest/**"

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/config/detekt.yml")
    baseline = file("$projectDir/config/baseline.xml")
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
    jvmTarget = "17"
    setSource(projectSource)
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles, testFiles, androidTestFiles)
}

tasks.register<DetektCreateBaselineTask>("detektGenerateBaseline") {
    description = "Custom DETEKT build to build baseline for all modules"
    setSource(projectSource)
    baseline.set(baselineFile)
    config.setFrom(configFile)
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles)
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
}

dependencyAnalysis {
    issues { all { onAny { severity("fail") } } }
}