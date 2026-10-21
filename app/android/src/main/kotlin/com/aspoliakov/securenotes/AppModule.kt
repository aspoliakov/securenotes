package com.aspoliakov.securenotes

import com.aspoliakov.securenotes.android.BuildConfig
import com.aspoliakov.securenotes.core_base.AppConfigProvider
import com.aspoliakov.securenotes.core_base.BuildType
import com.aspoliakov.securenotes.core_base.Environment
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val appModule = module {
    single<AppConfigProvider> {
        object : AppConfigProvider() {
            override fun getEnvironment(): Environment = Environment.valueOf(BuildConfig.FLAVOR.uppercase())
            override fun getVersionCode(): Int = BuildConfig.VERSION_CODE
            override fun getVersionName(): String = BuildConfig.VERSION_NAME
            override fun getBuildType(): BuildType = BuildType.valueOf(BuildConfig.BUILD_TYPE.uppercase())
        }
    }
}
