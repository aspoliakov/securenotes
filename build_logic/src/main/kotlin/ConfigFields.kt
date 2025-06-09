import com.android.build.api.dsl.ProductFlavor

/**
 * Project SecureNotes
 */
object ConfigFields {
    fun setup(flavor: ProductFlavor) {
        flavor.buildConfigField("String", "KEY", "\"VALUE\"")
    }
}