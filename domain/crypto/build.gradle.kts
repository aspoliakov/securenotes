private val moduleName = "domain_crypto"

plugins {
    alias(libs.plugins.commonModulePlugin)
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
    namespace = "${Config.APPLICATION_ID}.$moduleName"
}
