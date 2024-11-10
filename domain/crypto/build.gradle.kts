plugins {
    id(libs.plugins.commonAndroidPlugin.get().pluginId)
    id(libs.plugins.jetbrainsCompose.get().pluginId)
    id(libs.plugins.compose.compiler.get().pluginId)
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
            implementation(projects.core.keyValueStorage)
            implementation(libs.firebase.firestore)
            implementation(libs.libsodium)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.domain_crypto"
}
