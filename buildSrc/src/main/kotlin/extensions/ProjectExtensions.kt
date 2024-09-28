package extensions

import utils.getLocalProperty
import org.gradle.api.Project

fun Project.getLocalProperty(propertyName: String): String {
    return getLocalProperty(propertyName, this)
}
