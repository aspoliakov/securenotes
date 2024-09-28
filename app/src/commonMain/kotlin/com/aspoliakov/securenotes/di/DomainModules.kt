package com.aspoliakov.securenotes.di

import com.aspoliakov.securenotes.domain_notes.di.notesDomainModule
import com.aspoliakov.securenotes.domain_user_state.di.userStateDomainModule

/**
 * Project SecureNotes
 */

val domainModule = listOf(
        userStateDomainModule,
        notesDomainModule,
)
