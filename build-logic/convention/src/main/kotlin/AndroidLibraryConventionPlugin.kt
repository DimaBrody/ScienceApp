import com.android.build.gradle.LibraryExtension
import com.brody.arxiv.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("brody.android.library.lower")
//        apply("plugin.serialization")
            }

            dependencies {
                add("implementation", project(":core:common"))
            }
        }
    }
}
