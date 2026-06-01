plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.jetbrainsKotlinMultiplatform)
    alias(libs.plugins.jetbrainsComposeCompiler)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    android {
        namespace = "${Config.APPLICATION_ID}.shared"
        compileSdk = Config.COMPILE_SDK_VERSION
    }
    listOf(
            iosArm64(),
            iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "app"
            binaryOptions["bundleId"] = "${Config.APPLICATION_ID}.shared"
            isStatic = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.features.about)
            implementation(projects.features.auth)
            implementation(projects.features.keys)
            implementation(projects.features.home)
            implementation(projects.features.note)
            implementation(projects.features.notesBrowser)
            implementation(projects.features.profile)

            implementation(projects.domain.notes)
            implementation(projects.domain.userState)
            implementation(projects.domain.crypto)

            api(projects.core.base)
            implementation(projects.core.db)
            implementation(projects.core.keyValueStorage)
            api(projects.core.presentation)
            api(projects.core.ui)
        }
        androidMain.dependencies {
            api(libs.androidx.appcompat)
            api(libs.androidx.ktx)
            api(libs.androidx.splashScreen)
        }
    }
}
