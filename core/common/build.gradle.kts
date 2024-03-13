import com.android.build.gradle.LibraryExtension

plugins {
    alias(libs.plugins.brody.android.library.lower)
//    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
}

android {
    namespace = "com.brody.arxiv.core.common"

}

dependencies {

}
