plugins {
    alias(libs.plugins.brody.android.arch.model.domain)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.brody.arxiv.summary.models.domain"
}

dependencies {
    implementation(libs.kotlin.serialization)
}