package com.aspoliakov.securenotes.di

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.AppComposableViewModel
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

@Composable
fun AppDI(
        appDeclaration: KoinAppDeclaration = {},
        content: @Composable () -> Unit
) = KoinApplication(
        application = {
            Napier.base(DebugAntilog())
            modules(
                    dataModule +
                            domainModule +
                            appComposableViewModelModule +
                            featureModules
            )
            appDeclaration()
        },
        content = content
)

val appComposableViewModelModule = module {
    factory { AppComposableViewModel(get()) }
}
