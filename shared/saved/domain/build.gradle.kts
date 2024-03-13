plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.saved.domain"
}

dependencies {
    implementation(project(":shared:papers:models:domain"))
    implementation(project(":shared:saved:models:domain"))

    implementation(libs.androidx.paging.runtime)
}