plugins {
    alias(libs.plugins.brody.android.library)
    alias(libs.plugins.brody.android.arch.data)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.brody.arxiv.shared.search.data"
}

dependencies {
    implementation(project(":core:threading"))
    
    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:subjects:models:domain"))
    implementation(project(":shared:subjects:models:data"))

//    implementation(project(":core:data:datastore"))

    implementation(libs.androidx.datastore)
}