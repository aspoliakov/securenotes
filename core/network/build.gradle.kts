plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_network"
}
