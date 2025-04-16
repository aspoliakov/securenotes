plugins {
    id(libs.plugins.commonAndroidPlugin.get().pluginId)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_network"
}
