plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.domain)
}
android {
    namespace = "com.brody.arxiv.shared.papers.domain"
}

dependencies {
    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:saved:domain"))
    implementation(project(":shared:subjects:models:domain"))
    implementation(project(":shared:papers:models:domain"))

    implementation(libs.androidx.paging.runtime)
}