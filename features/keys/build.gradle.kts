private val moduleName = "feature_keys"

plugins {
    alias(libs.plugins.commonModulePlugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.userState)
            implementation(projects.domain.crypto)
            implementation(projects.core.base)
            implementation(projects.core.presentation)
            implementation(projects.core.ui)
            implementation(projects.core.network)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.$moduleName"
}
