plugins {
    alias(libs.plugins.brody.android.arch.model.data)
    alias(libs.plugins.brody.android.room)
}
android {
    namespace = "com.brody.arxiv.shared.saved.models.data"
}

dependencies {
    implementation(project(":shared:saved:models:domain"))
    implementation(project(":shared:papers:models:domain"))
}