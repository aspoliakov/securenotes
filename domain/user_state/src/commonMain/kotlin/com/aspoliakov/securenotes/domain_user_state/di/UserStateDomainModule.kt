package com.aspoliakov.securenotes.domain_user_state.di

import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val userStateDomainModule = module {
    val firebaseAuth = Firebase.auth
    single {
        UserStateInteractor(
                keyValueStorage = get(),
                encryptedKeyValueStorage = get(),
                auth = firebaseAuth,
                databaseManager = get(),
        )
    }
    single {
        UserStateProvider(
                keyValueStorage = get(),
                auth = firebaseAuth,
        )
    }
}
