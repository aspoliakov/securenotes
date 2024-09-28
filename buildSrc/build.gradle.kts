plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.android.buildTools)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("common-android-plugin") {
            id = "common-android-plugin"
            implementationClass = "commons.CommonAndroidPlugin"
        }
        register("core-module-plugin") {
            id = "core-module-plugin"
            implementationClass = "commons.CoreModulePlugin"
        }
        register("feature-module-plugin") {
            id = "feature-module-plugin"
            implementationClass = "commons.FeatureModulePlugin"
        }
    }
}
