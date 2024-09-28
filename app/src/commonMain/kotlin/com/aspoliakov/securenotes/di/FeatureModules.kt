package com.aspoliakov.securenotes.di

import com.aspoliakov.securenotes.feature_auth.di.authViewModelModule
import com.aspoliakov.securenotes.feature_home.di.homeViewModelModule

/**
 * Project SecureNotes
 */

val featureModules = listOf(
        authViewModelModule,
        homeViewModelModule,
)
