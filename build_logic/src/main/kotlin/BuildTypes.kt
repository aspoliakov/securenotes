import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.gradle.internal.dsl.SigningConfig
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

abstract class BuildTypeCreator(
        val name: String,
        val release: Boolean = true
) {

    fun createOrConfig(
            signingConfig: SigningConfig?,
            container: NamedDomainObjectContainer<ApplicationBuildType>
    ): ApplicationBuildType {
        val buildType = container.findByName(name)
        return if (buildType != null) {
            container.getByName(name, configureAction(signingConfig))
        } else {
            container.create(name, configureAction(signingConfig))
        }
    }

    open fun configureAction(signingConfig: SigningConfig?): Action<in ApplicationBuildType> {
        return Action {
            this.signingConfig = signingConfig
            if (release) {
                this.isShrinkResources = true
                this.isMinifyEnabled = true
                proguardFiles("proguard-android.txt", "proguard-rules.pro")
            }
        }
    }
}

object BuildTypeRelease : BuildTypeCreator("release")
object BuildTypeDebug : BuildTypeCreator("debug", release = false)
