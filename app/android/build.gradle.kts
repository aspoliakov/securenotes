plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsComposeCompiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.gms)
}

android {

    val appName = "securenotes"

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
        SignConfig(appName).create(project, this)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName(appName)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), file("proguard-rules.pro"))
        }
        debug {
            signingConfig = signingConfigs.getByName(appName)
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
