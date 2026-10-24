private val moduleName = "domain_sync"

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
            implementation(projects.domain.notes)
            implementation(projects.domain.folders)
            implementation(projects.core.base)
            implementation(projects.core.keyValueStorage)
            implementation(projects.core.network)
        }
        androidMain.dependencies {
        }
    }
}
