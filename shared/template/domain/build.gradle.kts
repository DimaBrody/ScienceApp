plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.search.domain"
}

dependencies {
    implementation(project(":shared:subjects:models:domain"))
}