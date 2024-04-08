plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.shared.filters.presentation"
}

dependencies {
    implementation(projects.core.threading)

    implementation(projects.shared.settings.general.domain)
    implementation(projects.shared.settings.general.models.domain)
    implementation(projects.shared.papers.models.presentation)
    implementation(projects.shared.papers.models.domain)
    implementation(projects.shared.subjects.presentation)
    implementation(projects.shared.subjects.models.presentation)
}