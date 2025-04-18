plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
}

kotlin {
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
    namespace = "${Config.APPLICATION_ID}.feature_profile"
}
