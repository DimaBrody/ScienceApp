plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.onboarding"
}

dependencies {
    implementation(project(":features:onboarding:domain"))
    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:search:presentation"))
    implementation(project(":shared:subjects:presentation"))
    implementation(project(":shared:subjects:models:presentation"))

//    implementation(libs.androidx.accompanist.statusbar)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.collections.immutable)
}