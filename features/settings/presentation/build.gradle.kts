plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.settings.presentation"
}

dependencies {
    implementation(projects.core.threading)

    implementation(projects.shared.settings.domain)
    implementation(projects.shared.settings.models.domain)
}