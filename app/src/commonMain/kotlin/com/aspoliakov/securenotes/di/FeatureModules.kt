package com.aspoliakov.securenotes.di

import com.aspoliakov.securenotes.feature_about.di.aboutViewModelModule
import com.aspoliakov.securenotes.feature_auth.di.authViewModelModule
import com.aspoliakov.securenotes.feature_home.di.homeViewModelModule
import com.aspoliakov.securenotes.feature_note.di.noteViewModelModule
import com.aspoliakov.securenotes.feature_notes_browser.di.notesBrowserViewModelModule
import com.aspoliakov.securenotes.feature_profile.di.profileViewModelModule

/**
 * Project SecureNotes
 */

val featureModules = listOf(
        aboutViewModelModule,
        authViewModelModule,
        homeViewModelModule,
        noteViewModelModule,
        notesBrowserViewModelModule,
        profileViewModelModule,
        aboutViewModelModule,
)
