import com.bz.movies.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.exclude

class DependenciesConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            target.configurations.all {
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
//                exclude("androidx.versionedparcelable", "versionedparcelable")
//                exclude("androidx.vectordrawable", "vectordrawable-animated")
//                exclude("androidx.vectordrawable", "vectordrawable")
                exclude("androidx.drawerlayout", "drawerlayout")
                exclude("org.checkerframework", "checker-qual")
                exclude("androidx.viewpager", "viewpager")
                exclude("androidx.activity", "activity-ktx")
                exclude("androidx.collection", "collection-ktx")
                exclude("androidx.lifecycle", "lifecycle-runtime-ktx-android")
                exclude("androidx.lifecycle", "lifecycle-runtime-ktx")
                exclude("androidx.lifecycle", "lifecycle-viewmodel-ktx")
                exclude("androidx.navigation", "navigation-common-ktx")
                exclude("androidx.navigation", "navigation-runtime-ktx")
                exclude("com.google.dagger", "dagger-lint-aar")

                // force deps
                resolutionStrategy {
                    libs.libraryAliases.forEach {
                        val lib = libs.findLibrary(it).get().get().module
                        val version = libs.findLibrary(it).get().get().versionConstraint
                        logger.info("Forcing library $lib with version:$version")
                        force("$lib:$version")
                    }
                }
            }
        }
    }
}
