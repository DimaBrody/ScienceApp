plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.shared.papers.presentation"
}

dependencies {
    implementation(project(":core:threading"))

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.compose.paging)

    implementation(project(":shared:papers:domain"))
    implementation(project(":shared:saved:domain"))
    implementation(project(":shared:saved:models:domain"))
    implementation(project(":shared:papers:models:domain"))
    implementation(project(":shared:papers:models:presentation"))
    implementation(project(":shared:subjects:models:domain"))
}