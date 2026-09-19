package com.aspoliakov.securenotes.domain_folders.di

import com.aspoliakov.securenotes.domain_folders.FolderCryptoInteractor
import com.aspoliakov.securenotes.domain_folders.FolderInteractor
import com.aspoliakov.securenotes.domain_folders.FoldersListInteractor
import com.aspoliakov.securenotes.domain_folders.network.FoldersApiProvider
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val foldersDomainModule = module {
    single<FoldersApiProvider> { FoldersApiProvider() }
    single { FolderCryptoInteractor(get()) }
    single { FolderInteractor(get(), get(), get(), get(), get(), get(), get()) }
    single { FoldersListInteractor(get(), get(), get(), get(), get()) }
}
