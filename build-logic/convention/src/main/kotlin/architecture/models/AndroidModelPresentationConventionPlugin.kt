package architecture.models

import AndroidHiltConventionPlugin
import com.brody.arxiv.findPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidModelPresentationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
//            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply {
                apply("brody.android.library")
                apply("brody.android.library.compose")
            }

            dependencies {
            }
        }
    }
}
