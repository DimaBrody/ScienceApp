plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.settings.domain"
}

dependencies {
    implementation(project(":shared:settings:models:domain"))
    implementation(project(":shared:papers:models:presentation"))
}