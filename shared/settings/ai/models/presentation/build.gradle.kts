import com.android.build.gradle.internal.dsl.LibraryExtensionImpl

plugins {
    alias(libs.plugins.brody.android.arch.model.presentation)
}
android {
    namespace = "com.brody.arxiv.shared.settings.ai.models.presentation"
}

dependencies {
    implementation(projects.shared.settings.ai.models.domain)
}