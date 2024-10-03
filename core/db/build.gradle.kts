plugins {
    id(libs.plugins.commonAndroidPlugin.get().pluginId)
    id(libs.plugins.kotlinMultiplatform.get().pluginId)
    id(libs.plugins.androidLibrary.get().pluginId)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    // TODO temporary issue with ksp + room + kotlin 2.0 - https://issuetracker.google.com/issues/343408758#comment4
//    alias(libs.plugins.room)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.kotlin.coroutines)
            api(libs.kotlin.atomicfu)
            api(libs.napier)
            api(libs.koin.compose)
            api(libs.koin.core)
            api(libs.room.runtime)
            api(libs.sqlite.bundled)
            api(libs.sqlite)
        }
        androidMain.dependencies {
            api(libs.koin.android)
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.core_db"
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}

ksp {
    arg("room.schemaLocation", "${projectDir}/schemas")
}
// TODO temporary issue with ksp + room + kotlin 2.0 - https://issuetracker.google.com/issues/343408758#comment4
//room {
//    schemaDirectory("$projectDir/schemas")
//}
