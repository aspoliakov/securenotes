import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.LibraryProductFlavor
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra

typealias AppFlavor = ApplicationProductFlavor
typealias LibFlavor = LibraryProductFlavor

object FlavorDimensions {
    const val ENVIRONMENT = "environment"
}

abstract class BuildProductFlavor {

    abstract val flavorDimension: String
    abstract val flavorName: String

    abstract fun configureActionForApp(): Action<AppFlavor>

    abstract fun configureActionForLib(): Action<LibFlavor>

    fun createOrConfigForApp(container: NamedDomainObjectContainer<AppFlavor>): AppFlavor {
        val buildType = container.findByName(flavorName)
        return if (buildType != null) {
            container.getByName(flavorName, configureActionForApp())
        } else {
            container.create(flavorName, configureActionForApp())
        }
    }

    fun createOrConfigForLib(container: NamedDomainObjectContainer<LibFlavor>): LibFlavor {
        val buildType = container.findByName(flavorName)
        return if (buildType != null) {
            container.getByName(flavorName, configureActionForLib())
        } else {
            container.create(flavorName, configureActionForLib())
        }
    }
}

sealed class EnvironmentFlavor(
        override val flavorName: String,
        private val appIdSuffix: String? = null,
) : BuildProductFlavor() {
    override val flavorDimension = FlavorDimensions.ENVIRONMENT

    override fun configureActionForApp(): Action<AppFlavor> {
        return Action {
            dimension = flavorDimension
            applicationId = Config.APPLICATION_ID
            if (appIdSuffix != null) {
                this.applicationIdSuffix = ".$appIdSuffix"
                this.versionNameSuffix = "-$appIdSuffix"
            }

            ConfigFields.setup(this)

            if (this@EnvironmentFlavor is Dev) {
                setupDevConfig(this)
            }
        }
    }

    override fun configureActionForLib(): Action<LibFlavor> {
        return Action {
            dimension = flavorDimension

            ConfigFields.setup(this)

            if (this@EnvironmentFlavor is Dev) {
                setupDevConfig(this)
            }
        }
    }

    private fun setupDevConfig(flavor: ProductFlavor) {
        flavor.resourceConfigurations.addAll(listOf("en", "xxhdpi"))
        (flavor as ExtensionAware).extra["alwaysUpdateBuildId"] = false
        (flavor as ExtensionAware).extra["enableCrashlytics"] = false
    }

    object Master : EnvironmentFlavor(
            flavorName = "master",
    )

    object Dev : EnvironmentFlavor(
            flavorName = "dev",
            appIdSuffix = "debug",
    )

    object Beta : EnvironmentFlavor(
            flavorName = "beta",
            appIdSuffix = "beta",
    )

    object Prerelease : EnvironmentFlavor(
            flavorName = "prerelease",
            appIdSuffix = "prerelease",
    )
}
