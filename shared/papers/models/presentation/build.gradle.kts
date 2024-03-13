plugins {
    alias(libs.plugins.brody.android.arch.model.presentation)
}

android {
    namespace = "com.brody.arxiv.shared.papers.models.presentation"
}

dependencies {
    implementation(project(":shared:papers:models:domain"))
}