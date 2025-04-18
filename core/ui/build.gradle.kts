plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

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
    packageOfResClass = "${Config.APPLICATION_ID}.core_ui.resources"
    generateResClass = always
}

android {
    namespace = "${Config.APPLICATION_ID}.core_ui"
}
