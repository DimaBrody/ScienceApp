plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.brody.android.room.extended)
}
android {
    namespace = "com.brody.arxiv.shared.summary.data"
}

dependencies {
    implementation(projects.core.threading)
    implementation(project(":shared:summary:domain"))
    implementation(project(":shared:summary:models:domain"))
    implementation(projects.core.data.datastoreProto)

    implementation(projects.shared.summary.models.data)

    implementation(libs.androidx.datastore)
    implementation(libs.langdroid.summary)
}