package com.aspoliakov.securenotes.di

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.AppComposableState
import com.aspoliakov.securenotes.AppComposableViewModel
import com.aspoliakov.securenotes.domain.SyncStackInteractor
import com.aspoliakov.securenotes.domain_user_state.UserPrefsInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
import com.aspoliakov.securenotes.toAppGlobalState
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import org.koin.compose.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

@Composable
fun AppDI(
        appModule: Module,
        content: @Composable () -> Unit,
) {
    KoinApplication(
            configuration = koinConfiguration {
                Napier.base(DebugAntilog())
                modules(
                        appModule +
                                coreModules +
                                domainModule +
                                appComposableViewModelModule +
                                featureModules
                )
                appInit(
                        syncStackInteractor = koin.get<SyncStackInteractor>(),
                        userPrefsInteractor = koin.get<UserPrefsInteractor>(),
                )
            },
            content = content,
    )
}

val appComposableViewModelModule = module {
    viewModel {
        val userStateProvider = get<UserStateProvider>()
        val userPrefsInteractor = get<UserPrefsInteractor>()
        val userState = runBlocking { userStateProvider.getUserState() }
        val themeMode = runBlocking { userPrefsInteractor.getAppThemeMode() }
        AppComposableViewModel(
                userStateProvider = userStateProvider,
                userPrefsInteractor = userPrefsInteractor,
                initialState = AppComposableState(
                        globalState = userState.toAppGlobalState(),
                        themeMode = themeMode,
                ),
        )
    }
}

private fun appInit(
        syncStackInteractor: SyncStackInteractor,
        userPrefsInteractor: UserPrefsInteractor,
) {
    syncStackInteractor.sync()
    userPrefsInteractor.sync()
}
