package com.aspoliakov.securenotes.core_base.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val coreBaseModule = module {
    includes(coreBasePlatformModule)
}

internal expect val coreBasePlatformModule: Module
