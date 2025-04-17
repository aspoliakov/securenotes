plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.userState)
            implementation(projects.core.base)
            implementation(projects.core.presentation)
            implementation(projects.core.ui)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.feature_about"
}
