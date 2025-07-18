private val moduleName = "core_db"

plugins {
    alias(libs.plugins.commonModulePlugin)
    alias(libs.plugins.room)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            api(libs.kotlinx.coroutines)
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
    namespace = "${Config.APPLICATION_ID}.$moduleName"
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
