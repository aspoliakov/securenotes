import dev.detekt.gradle.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import utils.buildVersionCode
import utils.buildVersionName
import utils.getVersionProperties

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

    val versionProperties = getVersionProperties(rootDir.absolutePath)

    defaultConfig {
        applicationId = Config.APPLICATION_ID
        minSdk = Config.MIN_SDK_VERSION
        targetSdk = Config.TARGET_SDK_VERSION
        versionCode = buildVersionCode(versionProperties)
        versionName = buildVersionName(versionProperties)
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
            signingConfigs.getByName(appName).takeIf { it.storeFile?.isFile == true }?.let {
                signingConfig = it
            }
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

tasks.named("check") {
    dependsOn("lintMasterRelease")
}

tasks.named("detekt") {
    dependsOn("detektMasterRelease")
}

tasks.withType<Detekt>().matching { it.name == "detektMasterRelease" }.configureEach {
    // The Kotlin compilation classpath omits generated Java classes such as BuildConfig.
    classpath.setFrom(
        tasks.named<KotlinCompile>("compileMasterReleaseKotlin").map { it.libraries },
        tasks.named<JavaCompile>("compileMasterReleaseJavaWithJavac").flatMap { it.destinationDirectory },
    )
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    implementation(projects.app.shared)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.splashScreen)
    implementation(libs.play.services.auth)
}
