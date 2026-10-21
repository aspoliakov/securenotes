package utils

import java.io.FileInputStream
import java.util.Properties

fun getVersionProperties(rootDir: String): Properties {
    val properties = Properties()
    properties.load(FileInputStream("${rootDir}/tools/versioning/version.properties"))
    return properties
}

fun buildVersionCode(properties: Properties): Int {
    val major = Integer.valueOf(properties["major"] as String)
    val minor = Integer.valueOf(properties["minor"] as String)
    val patch = Integer.valueOf(properties["patch"] as String)
    return major * 1_000_000 + minor * 1_000 + patch
}

fun buildVersionName(properties: Properties): String {
    val major = Integer.valueOf(properties["major"] as String)
    val minor = Integer.valueOf(properties["minor"] as String)
    val patch = Integer.valueOf(properties["patch"] as String)
    return "$major.$minor.$patch"
}
