package com.aspoliakov.securenotes.domain_user_state.di

import com.aspoliakov.securenotes.domain_user_state.UserPrefsInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
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
                authApiProvider = authApiProvider,
                databaseManager = get(),
        )
    }
    single {
        UserPrefsInteractor(
                keyValueStorage = get(),
        )
    }
}
