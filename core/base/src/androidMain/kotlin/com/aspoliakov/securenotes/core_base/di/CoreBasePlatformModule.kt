package com.aspoliakov.securenotes.core_base.di

import com.aspoliakov.securenotes.core_base.DeviceConfigProvider
import com.aspoliakov.securenotes.core_base.PlatformDeviceConfigProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

internal actual val coreBasePlatformModule = module {
    singleOf(::PlatformDeviceConfigProvider) bind DeviceConfigProvider::class
}
