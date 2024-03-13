plugins {
    alias(libs.plugins.brody.android.arch.model.domain)
}
android {
    namespace = "com.brody.arxiv.search.models.domain"
}

dependencies {
    implementation(project(":shared:papers:models:presentation"))
}