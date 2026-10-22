package com.aspoliakov.securenotes

import android.app.Application
import com.aspoliakov.securenotes.auth_providers.AndroidGoogleSignInLauncher

/**
 * Project SecureNotes
 */

class App : Application() {

    // Created before any activity, so that it tracks the activities from the very start
    lateinit var googleSignInLauncher: AndroidGoogleSignInLauncher
        private set

    override fun onCreate() {
        super.onCreate()
        googleSignInLauncher = AndroidGoogleSignInLauncher(this)
    }
}
