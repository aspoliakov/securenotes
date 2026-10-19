package com.aspoliakov.securenotes.domain_user_state.di

import com.aspoliakov.securenotes.core_network.auth.AuthTokenProvider
import com.aspoliakov.securenotes.domain_user_state.*
import com.aspoliakov.securenotes.domain_user_state.network.AuthApiProvider
import com.aspoliakov.securenotes.domain_user_state.network.UserSettingsApiProvider
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val userStateDomainModule = module {
    val authApiProvider = AuthApiProvider()
    single {
        UserSettingsApiProvider(
                authTokenProvider = get(),
        )
    }
    single {
        UserStateProvider(
                keyValueStorage = get(),
        )
    }
    single {
        UserStateInteractor(
                keyValueStorage = get(),
        )
    }
    single {
        UserSessionInteractor(
                encryptedKeyValueStorage = get(),
        )
    }
    single {
        RefreshTokenInteractor(
                userSessionInteractor = get(),
                userLogoutInteractor = get(),
                authApiProvider = authApiProvider,
        )
    } bind AuthTokenProvider::class
    single {
        UserPrefsInteractor(
                keyValueStorage = get(),
                userStateProvider = get(),
                userSettingsApiProvider = get(),
        )
    }
    single {
        UserAuthInteractor(
                userStateInteractor = get(),
                userSessionInteractor = get(),
                userPrefsInteractor = get(),
                authApiProvider = authApiProvider,
        )
    }
    single {
        UserLogoutInteractor(
                userStateInteractor = get(),
                userStateProvider = get(),
                userSessionInteractor = get(),
                encryptedKeyValueStorage = get(),
                databaseManager = get(),
                authApiProvider = authApiProvider,
        )
    }
}
