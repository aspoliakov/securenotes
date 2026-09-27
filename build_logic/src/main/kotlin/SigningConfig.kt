import com.android.build.api.dsl.ApkSigningConfig
import java.io.FileInputStream
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
        FileInputStream(propsFile).use { props.load(it) }
        return container.create(name) {
            storeFile = delegate.file("${delegate.rootDir}/tools/signing/securenotes.jks")
            storePassword = props.getProperty("storePassword")
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
        }
    }
}
