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
