@file:Suppress("UnstableApiUsage")


pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

rootProject.name = "arxiv"
include(":app")

include(":core:data:network")
include(":core:designsystem")
include(":core:common")
include(":core:navigation")
include(":core:threading")

include(":features:onboarding:presentation")
include(":features:onboarding:domain")
include(":features:onboarding:data")

include(":shared:subjects:presentation")
include(":shared:subjects:domain")
include(":shared:subjects:data")
include(":shared:subjects:models:presentation")
include(":shared:subjects:models:domain")
include(":shared:subjects:models:data")

include(":shared:search:presentation")
include(":shared:search:models:presentation")