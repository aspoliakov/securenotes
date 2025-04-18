plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            api(libs.jetbrains.navigation.compose)
            api(libs.androidx.lifecycle.viewmodel)
        }
        androidMain.dependencies {
            api(libs.androidx.lifecycle.viewmodel.compose)
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_presentation"
}
