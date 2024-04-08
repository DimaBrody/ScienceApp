plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
}

android {
    namespace = "com.brody.arxiv.core.pdf.download"
}

dependencies {
    implementation(projects.core.threading)
    implementation(projects.core.data.network)
}
