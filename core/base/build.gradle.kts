plugins {
    alias(libs.plugins.commonModulePlugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.kotlinx.coroutines)
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.serialization)
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
