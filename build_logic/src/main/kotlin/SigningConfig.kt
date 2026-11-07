import com.android.build.api.dsl.ApkSigningConfig
import java.util.Properties
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class SignConfig(val name: String) {

    fun create(
            delegate: Project,
            container: NamedDomainObjectContainer<out ApkSigningConfig>,
    ): ApkSigningConfig {
        val props = Properties()
        val propsFile = delegate.file("${delegate.rootDir}/tools/signing/signing.properties")
        if (propsFile.isFile) {
            propsFile.inputStream().use { props.load(it) }
        }
        fun value(
                environmentName: String,
                propertyName: String,
        ): String? {
            return delegate.providers.environmentVariable(environmentName).orNull
                    ?: props.getProperty(propertyName)
        }
        return container.create(name) {
            storeFile = delegate.rootProject.file(
                    value("SECURENOTES_KEYSTORE_FILE", "storeFile") ?: "tools/signing/securenotes.jks"
            )
            storePassword = value("SECURENOTES_STORE_PASSWORD", "storePassword")
            keyAlias = value("SECURENOTES_KEY_ALIAS", "keyAlias")
            keyPassword = value("SECURENOTES_KEY_PASSWORD", "keyPassword")
        }
    }
}
