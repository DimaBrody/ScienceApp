plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
}

android {
    namespace = "com.brody.arxiv.core.notifications"
}

dependencies {
    implementation(projects.core.designsystem)

    implementation(projects.shared.saved.models.domain)
    implementation(projects.features.summary.navigation)

    implementation(libs.kotlinx.coroutines.android)
}
