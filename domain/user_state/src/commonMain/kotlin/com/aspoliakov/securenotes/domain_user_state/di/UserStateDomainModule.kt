package com.aspoliakov.securenotes.domain_user_state.di

import com.aspoliakov.securenotes.domain_user_state.*
import com.aspoliakov.securenotes.domain_user_state.network.AuthApiProvider
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val userStateDomainModule = module {
    val authApiProvider = AuthApiProvider()
    single {
        UserStateProvider(
                keyValueStorage = get(),
        )
    }
    single {
        UserStateInteractor(
                keyValueStorage = get(),
                encryptedKeyValueStorage = get(),
                databaseManager = get(),
        )
    }
    single {
        UserPrefsInteractor(
                keyValueStorage = get(),
        )
    }
    single {
        UserAuthInteractor(
                userStateInteractor = get(),
                authApiProvider = authApiProvider,
        )
    }
    single {
        UserLogoutInteractor(
                userStateInteractor = get(),
        )
    }
}
