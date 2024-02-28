plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.subjects.domain"
}

dependencies {
    implementation(project(":shared:subjects:models:domain"))
}