private val moduleName = "domain_user_state"

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
            implementation(projects.core.db)
            implementation(projects.core.keyValueStorage)
            implementation(projects.core.network)
        }
        androidMain.dependencies {
        }
    }
}
