plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
    alias(libs.plugins.atomicfuPlugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(libs.kotlinx.atomicfu)
            api(libs.androidx.datastore.preferences.core)
            api(libs.kvault)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_key_value_storage"
}
