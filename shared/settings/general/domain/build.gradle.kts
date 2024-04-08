plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.settings.general.domain"
}

dependencies {
    implementation(project(":shared:settings:general:models:domain"))
    implementation(project(":shared:papers:models:presentation"))

    implementation(project(":shared:summary:domain"))
    implementation(project(":shared:summary:models:domain"))
}