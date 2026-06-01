package commons

import Config
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class CommonModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.androidKotlinMultiplatformLibrary.get().pluginId)
                apply(libs.plugins.jetbrainsKotlinMultiplatform.get().pluginId)
                apply(libs.plugins.jetbrainsComposeCompiler.get().pluginId)
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
        iosArm64()
        iosSimulatorArm64()
    }
}

internal fun Project.configureCommonAndroid() {
    extensions.getByType<KotlinMultiplatformExtension>()
        .extensions
        .configure<KotlinMultiplatformAndroidLibraryExtension> {
            compileSdk = Config.COMPILE_SDK_VERSION
            minSdk = Config.MIN_SDK_VERSION
        }
}
