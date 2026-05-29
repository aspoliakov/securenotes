private val moduleName = "core_presentation"

plugins {
    alias(libs.plugins.commonModulePlugin)
}

kotlin {
    android {
        namespace = "${Config.APPLICATION_ID}.$moduleName"
    }
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
