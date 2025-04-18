plugins {
    id(libs.plugins.commonModulePlugin.get().pluginId)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            api(libs.kotlin.coroutines)
            api(libs.kotlin.atomicfu)
            api(libs.napier)
            api(libs.room.runtime)
            api(libs.sqlite.bundled)
            api(libs.sqlite)
        }
        androidMain.dependencies {
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

room {
    schemaDirectory("$projectDir/schemas")
}
