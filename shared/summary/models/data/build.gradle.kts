plugins {
    alias(libs.plugins.brody.android.arch.model.data)
    alias(libs.plugins.brody.android.room)
}
android {
    namespace = "com.brody.arxiv.shared.summary.models.data"
}

dependencies {
    implementation(projects.shared.summary.models.domain)
}