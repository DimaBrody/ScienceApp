plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
}
android {
    namespace = "com.brody.arxiv.onboarding"
}

dependencies {
    implementation(project(":features:onboarding:domain"))
    implementation(project(":shared:subjects:domain"))
}