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
            implementation(projects.domain.userState)
            implementation(projects.core.base)
            implementation(projects.core.db)
            implementation(projects.core.keyValueStorage)
            implementation(libs.firebase.firestore)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.domain_notes"
}
