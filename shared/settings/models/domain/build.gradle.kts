plugins {
    alias(libs.plugins.brody.android.arch.model.domain)
}
android {
    namespace = "com.brody.arxiv.settings.models.domain"
}

dependencies {
    implementation(projects.shared.papers.models.presentation)
    implementation(projects.shared.papers.models.domain)
}