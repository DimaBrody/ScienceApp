plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
}

android {
    namespace = "com.brody.arxiv.core.network"
}

dependencies {

    implementation(libs.androidx.startup)

    implementation(libs.androidx.compose.coil)
    implementation(libs.androidx.compose.coil.svg)
    api(libs.okhttp.logging)
    api(libs.retrofit.core)
    api(libs.retrofit.moshi)
}