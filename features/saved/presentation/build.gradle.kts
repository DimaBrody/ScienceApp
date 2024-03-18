plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.saved.presentation"
}

dependencies {
    implementation(project(":shared:papers:presentation"))
    implementation(project(":shared:papers:models:presentation"))
    implementation(projects.shared.saved.models.domain)
}