plugins {
    alias(libs.plugins.brody.android.arch.model.data)
}
android {
    namespace = "com.brody.arxiv.shared.settings.ai.models.data"
}

dependencies {
    implementation(projects.shared.settings.ai.models.domain)

    implementation(libs.langdroid.core)
}