plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.kotlin.coroutines)
            api(libs.kotlin.datetime)
            api(libs.kotlin.serialization)
            api(libs.napier)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
        }
        androidMain.dependencies {
            api(libs.koin.android)
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_base"
}
