plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsComposeCompiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.gms)
}

android {

    namespace = "${Config.APPLICATION_ID}.android"
    compileSdk = Config.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = Config.APPLICATION_ID
        minSdk = Config.MIN_SDK_VERSION
        targetSdk = Config.TARGET_SDK_VERSION
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
    }

    signingConfigs {
        create("master") {
//            keyAlias = getLocalProperty("signing.key.alias")
//            keyPassword = getLocalProperty("signing.key.password")
//            storeFile = file(getLocalProperty("signing.store.file"))
//            storePassword = getLocalProperty("signing.store.password")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-android.txt", "proguard-rules.pro")
        }
        debug {

        }
    }

    flavorDimensions.addAll(listOf(FlavorDimensions.ENVIRONMENT))

    productFlavors {
        EnvironmentFlavor.Master.createOrConfigForApp(this)
        EnvironmentFlavor.Beta.createOrConfigForApp(this)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.app.shared)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.splashScreen)
}
