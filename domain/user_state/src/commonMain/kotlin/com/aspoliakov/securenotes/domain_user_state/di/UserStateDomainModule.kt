package com.aspoliakov.securenotes.domain_user_state.di

import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val userStateDomainModule = module {
    single {
        UserStateInteractor(
                keyValueStorage = get(),
                auth = Firebase.auth,
        )
    }
}
