private val moduleName = "domain_crypto"

plugins {
    alias(libs.plugins.commonModulePlugin)
    alias(libs.plugins.ktorfit)
}

kotlin {
    android {
        namespace = "${Config.APPLICATION_ID}.$moduleName"
    }
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
