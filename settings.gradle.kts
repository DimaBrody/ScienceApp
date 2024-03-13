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
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

rootProject.name = "arxiv"
include(":app")

include(":core:data:network")
include(":core:data:datastore-proto")
include(":core:designsystem")
include(":core:common")
include(":core:navigation")
include(":core:threading")

include(":features:onboarding:presentation")
include(":features:papers:presentation")
include(":features:explore:presentation")
include(":features:saved:presentation")
include(":features:settings:presentation")
include(":features:details:presentation")

include(":shared:subjects:presentation")
include(":shared:subjects:domain")
include(":shared:subjects:data")
include(":shared:subjects:models:presentation")
include(":shared:subjects:models:domain")
include(":shared:subjects:models:data")

include(":shared:search:presentation")
include(":shared:search:models:presentation")

include(":shared:papers:presentation")
include(":shared:papers:domain")
include(":shared:papers:data")
include(":shared:papers:models:presentation")
include(":shared:papers:models:domain")
include(":shared:papers:models:data")

include(":shared:saved:domain")
include(":shared:saved:data")
include(":shared:saved:models:domain")
include(":shared:saved:models:data")

include(":shared:filters:presentation")
include(":shared:filters:models:presentation")

include(":shared:settings:domain")
include(":shared:settings:data")
include(":shared:settings:models:domain")
include(":shared:settings:models:data")

