plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
}

android {
    namespace = "com.brody.arxiv.core.pdf.extract"
}

dependencies {
    implementation(projects.core.threading)
    implementation(libs.android.pdfbox)
}
