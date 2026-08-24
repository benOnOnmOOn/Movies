pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com.android.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com.google.*")
                includeGroupByRegex("org.chromium.net.*")
                excludeGroup("com.google.auto.service")
            }
        }
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.ben-manes.versions.settings") version "0.61.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com.android.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com.google.*")
                includeGroupByRegex("org.chromium.net.*")
                excludeGroup("com.google.auto.service")
            }
        }
        mavenCentral()
    }
}
rootProject.name = "Movies"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":data:network")
include(":presentation:screens")
include(":presentation:core")
include(":data:database")
include(":data:dto")
include(":utlis:android")
include(":app-lite")
include(":data:cronet")
include(":data:okhttp")
include(":data:datastore")
include(":presentation:core-lite")
include(":utlis:leakcanary")
include(":utlis:leakstub")
