plugins {
    alias(libs.plugins.brody.android.arch.model.data)
}
android {
    namespace = "com.brody.arxiv.shared.search.models.data"
}

dependencies {
    implementation(project(":shared:subjects:models:domain"))
}