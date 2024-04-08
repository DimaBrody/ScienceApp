plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.settings.presentation"
}

dependencies {
    implementation(projects.core.threading)

    implementation(projects.shared.settings.ai.presentation)

    implementation(projects.shared.settings.general.domain)
    implementation(projects.shared.settings.general.models.domain)
    implementation(projects.shared.summary.models.domain)
}