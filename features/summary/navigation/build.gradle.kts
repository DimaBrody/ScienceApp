import com.android.build.gradle.internal.dsl.LibraryExtensionImpl

plugins {
    alias(libs.plugins.brody.android.arch.model.data)
}
android {
    namespace = "com.brody.arxiv.features.summary.navigation"
}

dependencies {
    implementation(projects.shared.saved.models.domain)
}