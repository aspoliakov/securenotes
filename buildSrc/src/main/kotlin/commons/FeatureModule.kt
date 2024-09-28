package commons

import BuildTypeDebug
import BuildTypeRelease
import Config
import com.android.build.gradle.LibraryExtension
import extensions.implementation
import extensions.kotlinOptions
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

class FeatureModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = the<LibrariesForLibs>()

            with(pluginManager) {
                apply(libs.plugins.androidLibrary.get().pluginId)
                apply(libs.plugins.kotlinAndroid.get().pluginId)
            }

            dependencies {
                implementation(libs.kotlin)
            }

            extensions.getByType<LibraryExtension>().apply {

                namespace = Config.APPLICATION_ID
                compileSdk = Config.COMPILE_SDK_VERSION

                defaultConfig {
                    minSdk = Config.MIN_SDK_VERSION
                }

                buildTypes {
                    getByName(BuildTypeRelease.name) {}
                    getByName(BuildTypeDebug.name) {}
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }

                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_21.toString()
                }

                sourceSets.forEach {
                    it.java.setSrcDirs(it.java.srcDirs + "src/$it.name/kotlin")
                }

                testOptions {
                    unitTests.isIncludeAndroidResources = false
                }

                buildFeatures {
                    viewBinding = true
                    compose = true
                    buildConfig = true
                }
            }
        }
    }
}
