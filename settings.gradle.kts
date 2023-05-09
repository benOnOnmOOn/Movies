pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version ("3.13.1")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com.android.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com.google.*")
            }
        }
        mavenCentral()
    }
}
rootProject.name = "movies"

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

include(":app")
include(":data:network")
