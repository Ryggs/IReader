rootProject.name = "Infinity"
include(":app")
include(":core-ui")
include(":data")
include(":domain")
include(":core")
include(":presentation")
include(":source")
include(":extensions")


pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}
