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
        maven("https://jitpack.io")
    }
}

rootProject.name = "arxiv"

includeBuild("../langdroid-core/langdroid") {
    dependencySubstitution {
        substitute(module("com.arxiv.langdroid:core")).using(project(":core"))
        substitute(module("com.arxiv.langdroid:summary")).using(project(":summary"))
    }
}

include(":app")

include(":core:data:network")
include(":core:data:datastore-proto")
include(":core:designsystem")
include(":core:common")
include(":core:navigation")
include(":core:threading")
include(":core:pdf:download")
include(":core:pdf:extract")
include(":core:notifications")

include(":work:summary")

include(":features:onboarding:presentation")
include(":features:papers:presentation")
include(":features:explore:presentation")
include(":features:saved:presentation")
include(":features:settings:presentation")
include(":features:details:presentation")
include(":features:summary:presentation")
include(":features:summary:navigation")

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

include(":shared:settings:general:domain")
include(":shared:settings:general:data")
include(":shared:settings:general:models:domain")

include(":shared:settings:ai:domain")
include(":shared:settings:ai:data")
include(":shared:settings:ai:presentation")
include(":shared:settings:ai:models:domain")
include(":shared:settings:ai:models:data")
include(":shared:settings:ai:models:presentation")

include(":shared:summary:domain")
include(":shared:summary:data")
include(":shared:summary:models:domain")
include(":shared:summary:models:data")


