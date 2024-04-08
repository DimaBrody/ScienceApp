import com.google.protobuf.gradle.GenerateProtoTask

plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.brody.arxiv.shared.settings.general.data"
}


dependencies {
    implementation(projects.core.threading)
    implementation(project(":shared:settings:general:domain"))
    implementation(project(":shared:settings:general:models:domain"))
    implementation(projects.core.data.datastoreProto)

    implementation(libs.androidx.datastore)
}