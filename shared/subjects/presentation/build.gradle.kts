plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.shared.subjects.presentation"
}

dependencies {
    implementation(projects.core.threading)

    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:subjects:models:domain"))
    implementation(project(":shared:subjects:models:presentation"))
    implementation(project(":shared:search:models:presentation"))
}