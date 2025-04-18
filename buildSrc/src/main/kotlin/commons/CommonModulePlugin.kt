package commons

import BuildTypeDebug
import BuildTypeRelease
import Config
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

class CommonModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = the<LibrariesForLibs>()

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

            val extension = extensions.getByType<LibraryExtension>()
            configureCommonAndroid(libs, extension)
        }
    }
}

internal fun Project.configureCommonAndroid(
        libs: LibrariesForLibs,
        commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {

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

        testOptions {
            unitTests.isIncludeAndroidResources = false
        }

        buildFeatures {
            viewBinding = true
            compose = true
            buildConfig = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.versions.kotlin.get().toString()
        }
    }

    // Workaround to avoid task "testClasses" not found on "Rebuild project"
    task("testClasses")
}
