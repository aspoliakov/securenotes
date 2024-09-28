package com.aspoliakov.securenotes.feature_about.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel

/**
 * Project SecureNotes
 */

class AboutViewModel(
        initialState: AboutState,
) : MviViewModel<AboutState, AboutEffect, AboutIntent>(initialState) {

    override fun handleIntent(intent: AboutIntent) {
    }
}
