package com.aspoliakov.securenotes.di

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.AppComposableViewModel
import com.aspoliakov.securenotes.domain.SyncStackInteractor
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.viewModel
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
            appInit(
                    syncStackInteractor = koin.get<SyncStackInteractor>(),
            )
        },
        content = content
)

val appComposableViewModelModule = module {
    viewModel { AppComposableViewModel(get()) }
}

private fun appInit(
        syncStackInteractor: SyncStackInteractor,
) {
    syncStackInteractor.sync()
}