plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.summary.domain"
}

dependencies {
    implementation(project(":shared:summary:models:domain"))
    implementation(project(":shared:saved:domain"))
    implementation(project(":shared:saved:models:domain"))

    implementation(libs.langdroid.summary)
}