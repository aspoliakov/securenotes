private val moduleName = "core_network"

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
            api(libs.ktor.client.serialization)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.client.logging)
            api(libs.ktor.serialization.kotlinx.json)
            api(libs.ktorfit.lib)
        }
    }
}
