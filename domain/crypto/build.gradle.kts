plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
    alias(libs.plugins.ktorfit)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.keyValueStorage)
            implementation(projects.core.network)
            implementation(projects.domain.userState)
            implementation(libs.libsodium)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.domain_crypto"
}
