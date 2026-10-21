package com.aspoliakov.securenotes

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
            override fun getEnvironment(): Environment {
                return Environment.BETA
            }

            override fun getVersionCode(): Int {
                return 1001
            }

            override fun getVersionName(): String {
                return "0.1.1-beta"
            }

            override fun getBuildType(): BuildType {
                return BuildType.DEBUG
            }
        }
    }
}
