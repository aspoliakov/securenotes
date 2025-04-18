plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
    alias(libs.plugins.ktorfit)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.userState)
            implementation(projects.domain.crypto)
            implementation(projects.core.base)
            implementation(projects.core.db)
            implementation(projects.core.keyValueStorage)
            implementation(projects.core.network)
            implementation(libs.ktorfit.lib)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.domain_notes"
}
