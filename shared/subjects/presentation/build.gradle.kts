plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.shared.subjects.presentation"
}

dependencies {
    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:subjects:models:domain"))
    implementation(project(":shared:subjects:models:presentation"))
}