import com.google.protobuf.gradle.GenerateProtoTask

plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.brody.arxiv.shared.settings.data"
}

androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
//            val protoTask =
//                project.tasks.getByName("generate" + variant.name.replaceFirstChar { it.uppercaseChar() } + "Proto") as GenerateProtoTask

//            project.tasks.getByName("ksp" + variant.name.replaceFirstChar { it.uppercaseChar() } + "Kotlin") {
//                dependsOn(protoTask)
//                (this as org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool<*>).setSource(
//                    protoTask.outputBaseDir
//                )
//            }
        }
    }
}

dependencies {
    implementation(projects.core.threading)
    implementation(projects.shared.settings.domain)
    implementation(projects.shared.settings.models.domain)
    api(projects.core.data.datastoreProto)

    implementation(libs.androidx.datastore)
}