private val moduleName = "core_ui"

plugins {
    alias(libs.plugins.commonModulePlugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
            api(compose.material3)
            api(compose.materialIconsExtended)
            api(compose.ui)
            api(compose.components.resources)
            api(compose.components.uiToolingPreview)
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

android {
    namespace = "${Config.APPLICATION_ID}.$moduleName"
}
