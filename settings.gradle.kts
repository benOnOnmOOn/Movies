pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com.android.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com.google.*")
                includeGroupByRegex("org.chromium.net.*")
            }
        }
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version ("3.13.1")
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
            }
        }
        mavenCentral()
    }
}
rootProject.name = "Movies"

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

include(":app")
include(":data:network")
include(":presentation:screens")
include(":presentation:core")
include(":data:database")
include(":data:dto")
include(":utlis:android")
