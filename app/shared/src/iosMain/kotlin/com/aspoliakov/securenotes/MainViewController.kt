package com.aspoliakov.securenotes

import androidx.compose.ui.window.ComposeUIViewController
import com.aspoliakov.securenotes.core_base.AppConfigProvider
import com.aspoliakov.securenotes.di.AppDI
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun mainViewController(appConfigProvider: AppConfigProvider): UIViewController {
    val appModule = module {
        single<AppConfigProvider> { appConfigProvider }
    }
    return ComposeUIViewController {
        AppDI(appModule) {
            MainAppComposable()
        }
    }
}
