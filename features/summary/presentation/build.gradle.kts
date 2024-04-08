plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.presentation)
}
android {
    namespace = "com.brody.arxiv.features.summary.presentation"
}

dependencies {
    implementation(projects.core.threading)
    implementation(projects.core.pdf.download)
    implementation(projects.work.summary)

    implementation(projects.shared.saved.models.domain)
    implementation(projects.shared.summary.models.domain)
    implementation(projects.shared.summary.domain)
    implementation(projects.shared.settings.ai.domain)
    implementation(projects.shared.settings.ai.presentation)
    implementation(projects.shared.settings.ai.models.domain)

    implementation(projects.features.summary.navigation)

    implementation(libs.kotlin.serialization)
    implementation(libs.compose.markdown)
//    implementation("com.github.takahirom.intellij-markdown:markdown-jvm:kotlin-js-clean-SNAPSHOT")
}