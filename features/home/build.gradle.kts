private val moduleName = "feature_home"

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
            implementation(projects.core.presentation)
            implementation(projects.core.ui)
        }
        androidMain.dependencies {
        }
    }
}
