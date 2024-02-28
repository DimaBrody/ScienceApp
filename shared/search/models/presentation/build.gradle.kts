import com.android.build.gradle.internal.dsl.LibraryExtensionImpl

plugins {
    alias(libs.plugins.brody.android.arch.model.presentation)
}
android {
    namespace = "com.brody.arxiv.shared.search.models.presentation"
}

dependencies {
    implementation(project(":shared:subjects:models:domain"))
    implementation(project(":shared:subjects:models:presentation"))
    implementation(libs.androidx.compose.ui)
}