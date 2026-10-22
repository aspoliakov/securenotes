package com.aspoliakov.securenotes

import androidx.compose.ui.window.ComposeUIViewController
import com.aspoliakov.securenotes.core_base.AppConfigProvider
import com.aspoliakov.securenotes.di.AppDI
import com.aspoliakov.securenotes.feature_auth.presentation.auth_providers.GoogleSignInLauncher
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
