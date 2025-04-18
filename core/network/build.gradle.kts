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
            implementation(projects.core.base)
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_network"
}
