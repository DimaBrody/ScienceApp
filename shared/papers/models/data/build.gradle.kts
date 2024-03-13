plugins {
    alias(libs.plugins.brody.android.arch.model.data)
    alias(libs.plugins.brody.android.room)
}
android {
    namespace = "com.brody.arxiv.shared.papers.models.data"
}

dependencies {
    implementation(project(":shared:papers:models:domain"))
    implementation(libs.android.xmlutil)
}