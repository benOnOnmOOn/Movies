import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import java.util.Locale

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.gradle.versions) apply true
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependency.analysis) apply true
    jacoco
    java
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


jacoco {
    toolVersion = "0.8.7"
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

project.afterEvaluate {
    val variants = listOf("debug", "prodNormalDebug")

    tasks.create<JacocoReport>("allDebugCoverage") {

        group = "Reporting"
        description = "Generate overall Jacoco coverage report for the debug build."

        reports {
            xml.required.set(true)
            csv.required.set(true)
        }

        val excludes = listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "androidx/**/*.*",
            "**/*${'$'}ViewInjector*.*",
            "**/*Dagger*.*",
            "**/*MembersInjector*.*",
            "**/*_Factory.*",
            "**/*_Provide*Factory*.*",
            "**/*_ViewBinding*.*",
            "**/AutoValue_*.*",
            "**/R2.class",
            "**/R2$*.class",
            "**/*Directions$*",
            "**/*Directions.*",
            "**/*Binding.*"
        )

        val jClasses = subprojects.mapNotNull { proj ->
            variants.map { variant ->
                "${proj.buildDir}/intermediates/javac/$variant/classes"
            }
        }.flatten()
        val kClasses = subprojects.mapNotNull { proj ->
            variants.map { variant ->
                "${proj.buildDir}/tmp/kotlin-classes/$variant"
            }
        }.flatten()

        val javaClasses = jClasses
            .filter { path ->
                val file = File(path)
                file.exists() && !file.isDirectory
            }.mapNotNull { path ->
                fileTree(mapOf(path to excludes))
            }

        val kotlinClasses = kClasses.filter { path ->
            val file = File(path)
            file.exists() && !file.isDirectory
        }.mapNotNull { path ->
            fileTree(mapOf(path to excludes))
        }

        classDirectories.setFrom(files(javaClasses, kotlinClasses))
        val sources = subprojects.mapNotNull { proj ->
            variants.map { variant ->
                listOf(
                    "${proj.projectDir}/src/main/java",
                    "${proj.projectDir}/src/main/kotlin",
                    "${proj.projectDir}/src/$variant/java",
                    "${proj.projectDir}/src/$variant/kotlin"
                )
            }.flatten()
        }.flatten()
        sourceDirectories.setFrom(files(sources))

        val executions = subprojects.mapNotNull { proj ->
            variants.mapNotNull { variant ->
                val path = "${proj.buildDir}/jacoco/test${
                    variant.replaceFirstChar { it.titlecase() }
                }UnitTest.exec"
                if (File(path).exists()) path else null
            }
        }.flatten()

        executionData.setFrom(files(executions))
    }
}
