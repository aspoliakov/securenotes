package commons

import BuildTypeDebug
import BuildTypeRelease
import Config
import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class CommonModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.androidLibrary.get().pluginId)
                apply(libs.plugins.kotlinMultiplatform.get().pluginId)
                apply(libs.plugins.composeCompiler.get().pluginId)
                apply(libs.plugins.jetbrainsCompose.get().pluginId)
                apply(libs.plugins.ksp.get().pluginId)
                apply(libs.plugins.kotlinSerialization.get().pluginId)
            }

            dependencies {
            }

            configureKotlin()
            configureCommonAndroid()
        }
    }
}

internal val Project.libs get() = this.the<LibrariesForLibs>()

internal fun Project.configureKotlin() {
    extensions.getByType<KotlinMultiplatformExtension>().apply {
        androidTarget()
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }
}

internal fun Project.configureCommonAndroid() {
    extensions.getByType<LibraryExtension>().apply {
        compileSdk = Config.COMPILE_SDK_VERSION

        defaultConfig {
            minSdk = Config.MIN_SDK_VERSION
        }

        buildTypes {
            getByName(BuildTypeRelease.name) {}
            getByName(BuildTypeDebug.name) {}
        }

        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
            targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        }
    }
}
