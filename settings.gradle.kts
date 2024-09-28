enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.buildFileName = "build.gradle.kts"

include(":app")

include(":features:about", "features/about")
include(":features:auth", "features/auth")
include(":features:home", "features/home")
include(":features:note", "features/note")
include(":features:notes_browser", "features/notes_browser")
include(":features:profile", "features/profile")

include(":domain:notes", "domain/notes")
include(":domain:user_state", "domain/user_state")

include(":core:base", "core/base")
include(":core:db", "core/db")
include(":core:key_value_storage", "core/key_value_storage")
include(":core:presentation", "core/presentation")
include(":core:ui", "core/ui")

fun include(path: String, projectDir: String) {
    include(path)
    project(path).projectDir = File(projectDir)
}