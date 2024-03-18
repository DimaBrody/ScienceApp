plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.details.presentation"
}

dependencies {
    implementation(projects.core.threading)

    implementation(projects.shared.saved.domain)
    implementation(projects.shared.saved.models.domain)
    implementation(projects.shared.papers.models.presentation)

    implementation(libs.androidx.browser)
    implementation(libs.kotlin.serialization)
    implementation(libs.compose.markdown)
}