plugins {
    id(libs.plugins.kotlinMultiplatform.get().pluginId)
    id(libs.plugins.androidApplication.get().pluginId)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget()

    listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "app"
            binaryOptions["bundleId"] = Config.APPLICATION_ID
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
            implementation(projects.core.base)
            implementation(projects.core.db)
            implementation(projects.core.keyValueStorage)
            implementation(projects.core.presentation)
            implementation(projects.core.ui)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.splashScreen)
        }
    }
}

android {

    namespace = Config.APPLICATION_ID
    compileSdk = Config.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = Config.APPLICATION_ID
        minSdk = Config.MIN_SDK_VERSION
        targetSdk = Config.TARGET_SDK_VERSION
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
    }

    signingConfigs {
        create(BuildTypeRelease.name) {
//            keyAlias = getLocalProperty("signing.key.alias")
//            keyPassword = getLocalProperty("signing.key.password")
//            storeFile = file(getLocalProperty("signing.store.file"))
//            storePassword = getLocalProperty("signing.store.password")
        }
    }

    buildTypes {
        BuildTypeRelease.createOrConfig(signingConfigs.getByName(BuildTypeRelease.name), this)
        BuildTypeDebug.createOrConfig(signingConfigs.getByName(BuildTypeDebug.name), this)
    }

    flavorDimensions.addAll(listOf(FlavorDimensions.ENVIRONMENT))

    productFlavors {
        EnvironmentFlavor.Master.createOrConfigForApp(this)
        EnvironmentFlavor.Dev.createOrConfigForApp(this)
        EnvironmentFlavor.Beta.createOrConfigForApp(this)
        EnvironmentFlavor.Prerelease.createOrConfigForApp(this)
    }

    buildFeatures {
        buildConfig = true
    }

    kotlin {
        jvmToolchain(21)
    }
}

//apply(plugin = libs.plugins.gms.get().pluginId)
