import com.google.protobuf.gradle.GenerateProtoTask

plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.brody.arxiv.core.data.proto"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
//
//androidComponents.beforeVariants {
//    android.sourceSets.register(it.name) {
//        val buildDir = layout.buildDirectory.get().asFile
//        java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
//        kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
//    }
//
//
//}

androidComponents.beforeVariants {
    android.sourceSets.forEach {
        val buildDir = layout.buildDirectory.get().asFile
        it.java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
        it.kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
    }
}


dependencies {
    api(libs.protobuf.kotlin.lite)
}
