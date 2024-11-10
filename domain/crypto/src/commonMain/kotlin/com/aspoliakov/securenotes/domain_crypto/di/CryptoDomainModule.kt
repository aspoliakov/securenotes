package com.aspoliakov.securenotes.domain_crypto.di

import com.aspoliakov.securenotes.domain_crypto.UserKeysInteractor
import com.aspoliakov.securenotes.domain_crypto.UserKeysProvider
import com.aspoliakov.securenotes.domain_crypto.network.KeysApi
import com.aspoliakov.securenotes.domain_crypto.network.KeysFirestoreApi
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val cryptoDomainModule = module {
    runBlocking { LibsodiumInitializer.initialize() }
    single<KeysApi> { KeysFirestoreApi(get(), Firebase.firestore) }
    single { UserKeysInteractor(get(), get(), get()) }
    single { UserKeysProvider(get()) }
}
