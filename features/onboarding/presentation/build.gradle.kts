plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.onboarding"
}

dependencies {
    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:search:presentation"))
    implementation(project(":shared:subjects:presentation"))
    implementation(project(":shared:subjects:models:presentation"))

//    implementation(libs.androidx.accompanist.statusbar)
    implementation(libs.kotlinx.collections.immutable)
}