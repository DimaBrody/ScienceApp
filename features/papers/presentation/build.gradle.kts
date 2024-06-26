plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.papers.presentation"
}

dependencies {
    implementation(projects.core.threading)

    implementation(project(":shared:papers:presentation"))
    implementation(project(":shared:papers:models:presentation"))
    implementation(project(":shared:filters:presentation"))
    implementation(projects.shared.saved.models.domain)

    implementation(projects.shared.settings.general.domain)
    implementation(projects.shared.settings.general.models.domain)
    implementation(projects.shared.papers.models.presentation)
}