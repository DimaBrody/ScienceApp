plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.settings.ai.domain"
}

dependencies {
    implementation(project(":shared:settings:ai:models:domain"))
}