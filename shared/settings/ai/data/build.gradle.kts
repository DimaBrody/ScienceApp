import com.google.protobuf.gradle.GenerateProtoTask

plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.brody.arxiv.shared.settings.ai.data"
}


dependencies {
    implementation(projects.core.threading)
    implementation(projects.core.data.datastoreProto)

    implementation(projects.shared.settings.ai.domain)
    implementation(projects.shared.settings.ai.models.domain)
    implementation(projects.shared.settings.ai.models.data)

    implementation(libs.langdroid.summary)
    implementation(libs.crypto.tink)
    implementation(libs.kotlin.serialization.protobuf)


    implementation(libs.androidx.datastore)
}