plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.shared.settings.ai.presentation"
}

dependencies {
    implementation(projects.core.threading)

    implementation(projects.shared.settings.ai.domain)
    implementation(projects.shared.settings.ai.models.domain)
    implementation(projects.shared.settings.ai.models.presentation)
}