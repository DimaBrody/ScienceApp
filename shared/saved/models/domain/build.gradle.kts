plugins {
    alias(libs.plugins.brody.android.arch.model.domain)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.brody.arxiv.search.models.domain"
}

dependencies {
    implementation(project(":shared:papers:models:presentation"))
    implementation(libs.kotlin.serialization)
}