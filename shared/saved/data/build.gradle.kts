plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.brody.android.room.extended)
}
android {
    namespace = "com.brody.arxiv.shared.saved.data"
}

dependencies {
    implementation(project(":shared:saved:domain"))
    implementation(project(":shared:saved:models:data"))
    implementation(project(":shared:saved:models:domain"))
    implementation(project(":shared:papers:models:domain"))

    implementation(libs.androidx.paging.runtime)
}