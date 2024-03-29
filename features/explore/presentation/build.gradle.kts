plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.explore.presentation"
}

dependencies {
    implementation(projects.shared.subjects.presentation)
    implementation(projects.shared.search.models.presentation)
}