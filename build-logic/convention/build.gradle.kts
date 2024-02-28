plugins {
    `kotlin-dsl`
}

group = "com.brody.arxiv.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "brody.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "brody.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "brody.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "brody.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidPresentation") {
            id = "brody.android.presentation"
            implementationClass = "architecture.AndroidPresentationConventionPlugin"
        }
        register("androidDomain") {
            id = "brody.android.domain"
            implementationClass = "architecture.AndroidDomainConventionPlugin"
        }
        register("androidData") {
            id = "brody.android.data"
            implementationClass = "architecture.AndroidDataConventionPlugin"
        }

        register("androidPresentationData") {
            id = "brody.android.model.presentation"
            implementationClass = "architecture.models.AndroidModelPresentationConventionPlugin"
        }
        register("androidDomainData") {
            id = "brody.android.model.domain"
            implementationClass = "architecture.models.AndroidModelDomainConventionPlugin"
        }
        register("androidModelData") {
            id = "brody.android.model.data"
            implementationClass = "architecture.models.AndroidModelDataConventionPlugin"
        }

        register("androidSerialization"){
            id = "brody.android.serialization"
            implementationClass = "AndroidSerializationConventionPlugin"
        }
        register("androidFeature") {
            id = "brody.android.feature"
            implementationClass = "architecture.AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "brody.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("spotless") {
            id = "brody.spotless"
            implementationClass = "SpotlessConventionPlugin"
        }
    }
}
