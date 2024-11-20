import org.gradle.kotlin.dsl.android

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dependency.guard)
    alias(libs.plugins.dexcount)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

android {
    namespace = "com.bz.movies"

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")

//            optimization {
//                keepRules {
//                    ignoreAllExternalDependencies(true)
//                }
//            }
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencyAnalysis {
    issues {
        onUnusedDependencies { exclude("com.squareup.leakcanary:leakcanary-android") }
        onUnusedDependencies { exclude(libs.androidx.appcompat) }
    }
}

apiValidation {
    ignoredPackages.add("hilt_aggregated_deps")
    nonPublicMarkers.addAll(
        listOf(
            "dagger.internal.DaggerGenerated",
            "javax.annotation.processing.Generated",
            "dagger.hilt.codegen.OriginatingElement"
        )
    )
}

kover {
    currentProject {
        createVariant("custom") {
            add("debug")
        }
    }
    reports {
        total {
            html {
                onCheck = true
            }
            xml {
                onCheck = true
            }
        }
        filters {
            excludes {
                classes(
                    // moshi json adapter
                    "com.bz.network.api.model.*JsonAdapter",
                    "*ComposableSingletons*",
                    "*_Factor*y",
                    "*_HiltModules*",
                    "*Hilt_*",
                    "*_Impl*"
                )
                packages(
                    "hilt_aggregated_deps",
                    "dagger.hilt.internal.aggregatedroot.codegen",
                    "com.bz.movies.database.dao",
                    "com.bz.movies.presentation.theme",
                    "com.bz.movies.presentation.navigation",
                    "com.bz.movies.presentation.screens.utils",
                    "com.bz.movies.core"
                )
                annotatedBy(
                    "*Generated*",
                    "*Composable*",
                    "*Module*",
                    "*HiltAndroidApp*",
                    "*AndroidEntryPoint*"
                )
            }
        }
    }
}

dependencies {
    // don't warn
    implementation(project(":presentation:core"))
    implementation(project(":data:okhttp"))
    kover(project(":presentation:core"))
    kover(project(":presentation:screens"))
    kover(project(":data:network"))
    kover(project(":data:database"))
    kover(project(":data:datastore"))

    lintChecks(libs.slack.lint.checks)

    //  HILT
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    implementation(libs.javax.inject)
    //

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.dagger)
    implementation(libs.kermit)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlin.stdlib)
    debugImplementation(libs.leakcanary.android)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    debugImplementation(libs.logging.interceptor)

    debugImplementation(libs.kermit.core.android.debug)
    debugImplementation(libs.kermit.android.debug)
    releaseImplementation(libs.kermit.core)

    debugRuntimeOnly(libs.androidx.ui.test.manifest)
    debugRuntimeOnly(libs.androidx.ui.tooling)

    testImplementation(libs.junit.api)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit.api)
    androidTestRuntimeOnly(libs.junit.engine)
}

dependencyGuard {
    // All dependencies included in Production Release APK
    configuration("releaseRuntimeClasspath")
}
