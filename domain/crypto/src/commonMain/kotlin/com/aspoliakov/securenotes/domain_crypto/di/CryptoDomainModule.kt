package com.aspoliakov.securenotes.domain_crypto.di

import com.aspoliakov.securenotes.domain_crypto.UserKeysInteractor
import com.aspoliakov.securenotes.domain_crypto.UserKeysProvider
import com.aspoliakov.securenotes.domain_crypto.network.KeysApiProvider
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val cryptoDomainModule = module {
    runBlocking { LibsodiumInitializer.initialize() }
    single<KeysApiProvider> { KeysApiProvider() }
    single { UserKeysInteractor(get(), get(), get()) }
    single { UserKeysProvider(get()) }
}
