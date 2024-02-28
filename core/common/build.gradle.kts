plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
}

android {
    namespace = "com.brody.arxiv.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
