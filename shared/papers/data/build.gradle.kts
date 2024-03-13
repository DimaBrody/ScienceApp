plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.brody.android.room.extended)
}
android {
    namespace = "com.brody.arxiv.shared.papers.data"
}

dependencies {
    implementation(project(":core:threading"))
    implementation(project(":core:data:network"))

    implementation(project(":shared:papers:domain"))
    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:papers:models:domain"))
    implementation(project(":shared:papers:models:data"))

    implementation(libs.androidx.paging.runtime)
}