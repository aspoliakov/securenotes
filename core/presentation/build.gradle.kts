plugins {
    alias(libs.plugins.commonModulePlugin)
    alias(libs.plugins.atomicfuPlugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.ui)
            implementation(libs.kotlinx.atomicfu)
            api(libs.jetbrains.navigation.compose)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_presentation"
}
