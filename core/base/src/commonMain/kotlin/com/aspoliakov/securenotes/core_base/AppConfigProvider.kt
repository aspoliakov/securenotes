package com.aspoliakov.securenotes.core_base

/**
 * Project SecureNotes
 */
abstract class AppConfigProvider {

    companion object {
        const val MAJOR_VERSION_MULTIPLIER = 1000000
        const val MINOR_VERSION_MULTIPLIER = 1000
    }

    abstract fun getEnvironment(): Environment

    abstract fun getVersionCode(): Int

    abstract fun getVersionName(): String

    abstract fun getBuildType(): BuildType

    fun getAppVersion(): String {
        val versionCode = getVersionCode()
        val major = versionCode / MAJOR_VERSION_MULTIPLIER
        val minor = (versionCode - major * MAJOR_VERSION_MULTIPLIER) / MINOR_VERSION_MULTIPLIER
        val patch = versionCode - major * MAJOR_VERSION_MULTIPLIER - minor * MINOR_VERSION_MULTIPLIER
        return "$major.$minor.$patch"
    }

    fun getSemanticAppVersion(): String {
        val appVersion = getAppVersion()
        val buildType = getBuildType().name.lowercase()
        return "$appVersion+$buildType"
    }
}

enum class Environment {
    MASTER,
    BETA,
}

enum class BuildType {
    RELEASE,
    DEBUG,
}
