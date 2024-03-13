import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.brody.android.application)
    alias(libs.plugins.brody.android.application.compose)
    alias(libs.plugins.brody.android.hilt)
    alias(libs.plugins.brody.spotless)
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.google.secrets.get().pluginId)
    id(libs.plugins.baseline.profile.get().pluginId)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = File(rootProject.rootDir, "keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.brody.arxiv"

    defaultConfig {
        applicationId = "com.brody.arxiv"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["releaseKeyAlias"] as String?
            keyPassword = keystoreProperties["releaseKeyPassword"] as String?
            storeFile = file(keystoreProperties["releaseStoreFile"] ?: "release/release-key.jks")
            storePassword = keystoreProperties["releaseStorePassword"] as String?
        }
    }

    buildTypes {
        release {
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs["release"]
            }
            isShrinkResources = true
            isMinifyEnabled = true
        }

        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            proguardFiles("benchmark-rules.pro")
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {

    implementation(project(":core:designsystem"))
    implementation(project(":core:data:network"))

    implementation(project(":features:onboarding:presentation"))
    implementation(project(":features:papers:presentation"))
    implementation(project(":features:explore:presentation"))
    implementation(project(":features:saved:presentation"))
    implementation(project(":features:settings:presentation"))
    implementation(project(":features:details:presentation"))

    implementation(project(":shared:subjects:domain"))
    implementation(project(":shared:subjects:models:domain"))

    // DATA
    implementation(project(":shared:subjects:data"))
    implementation(project(":shared:papers:data"))
    implementation(project(":shared:saved:data"))
    implementation(project(":shared:settings:data"))


    // compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.splashscreen)
    implementation(libs.androidx.compose.coil)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.accompanist.statusbar)
    implementation(libs.androidx.navigation.compose)

    // jetpack
    implementation(libs.androidx.startup)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // crash tracer & restorer
    implementation(libs.snitcher)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
}