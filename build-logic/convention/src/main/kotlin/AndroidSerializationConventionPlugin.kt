import com.brody.arxiv.findPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidSerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply {
                apply(libs.findPluginId("kotlin.serialization"))
            }

            dependencies {
                add("implementation", libs.findLibrary("kotlin.serialization").get())
            }
        }
    }
}
