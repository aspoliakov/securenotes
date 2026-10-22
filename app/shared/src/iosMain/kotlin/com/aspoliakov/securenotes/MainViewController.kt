package com.aspoliakov.securenotes

import androidx.compose.ui.window.ComposeUIViewController
import com.aspoliakov.securenotes.core_base.AppConfigProvider
import com.aspoliakov.securenotes.core_base.GoogleSignInLauncher
import com.aspoliakov.securenotes.di.AppDI
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun mainViewController(
        appConfigProvider: AppConfigProvider,
        googleSignInLauncher: GoogleSignInLauncher,
): UIViewController {
    val appModule = module {
        single<AppConfigProvider> { appConfigProvider }
        single<GoogleSignInLauncher> { googleSignInLauncher }
    }
    return ComposeUIViewController {
        AppDI(appModule) {
            MainAppComposable()
        }
    }
}
