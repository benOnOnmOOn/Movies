import com.android.build.gradle.api.AndroidBasePlugin
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")

            /** Add support for Android modules, based on [AndroidBasePlugin] */
            pluginManager.withPlugin("com.android.base") {
                pluginManager.apply("dagger.hilt.android.plugin")
            }

            extensions.configure<KspExtension> {
                // Enable some compiler options https://dagger.dev/dev-guide/compiler-options
                arg("dagger.useBindingGraphFix", "ENABLED")
                arg("dagger.ignoreProvisionKeyWildcards", "ENABLED")
                arg("dagger.fullBindingGraphValidation", "ERROR")
            }
        }
    }
}
