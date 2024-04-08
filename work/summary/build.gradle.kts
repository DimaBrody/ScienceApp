plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.brody.arxiv.work.summary"
}

dependencies {
    implementation(projects.core.threading)
    implementation(projects.core.notifications)
    implementation(projects.core.pdf.extract)
    implementation(projects.core.pdf.download)

    implementation(projects.shared.summary.domain)
    implementation(projects.shared.summary.models.domain)
    implementation(projects.shared.settings.ai.domain)
    implementation(projects.shared.settings.ai.models.domain)
    implementation(projects.shared.settings.ai.models.data)
    implementation(projects.shared.saved.models.domain)

    implementation(libs.langdroid.summary)
    implementation(libs.kotlin.serialization)
    api(libs.androidx.worker)

    implementation(libs.hilt.worker)
    ksp(libs.hilt.androidx.compiler)
}
