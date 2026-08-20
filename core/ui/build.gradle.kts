private val moduleName = "core_ui"

plugins {
    alias(libs.plugins.commonModulePlugin)
}

kotlin {
    android {
        namespace = "${Config.APPLICATION_ID}.$moduleName"
        androidResources.enable = true
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrainsComposeRuntime)
            api(libs.jetbrainsComposeFoundation)
            api(libs.jetbrainsComposeMaterial)
            api(libs.jetbrainsComposeMaterial3)
            api(libs.jetbrainsComposeMaterialIconsExtended)
            api(libs.jetbrainsComposeUi)
            api(libs.jetbrainsComposeUiToolingPreview)
            api(libs.jetbrainsComposeComponentsResources)
        }
        androidMain.dependencies {
            api(libs.compose.ui.tooling)
            api(libs.compose.ui.tooling.preview)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "${Config.APPLICATION_ID}.$moduleName.resources"
    generateResClass = always
}
