package com.aspoliakov.securenotes.feature_sessions.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.domain_user_state.model.ActiveSession

/**
 * Project SecureNotes
 */

data class SessionsState(
        val contentState: SessionsContentState = SessionsContentState.Loading,
        val isTerminating: Boolean = false,
        val confirmation: SessionsConfirmation? = null,
) : State()

sealed class SessionsContentState {
    data object Loading : SessionsContentState()
    data object Error : SessionsContentState()
    data class Loaded(
            val currentSession: ActiveSession?,
            val otherSessions: List<ActiveSession>,
    ) : SessionsContentState()
}

sealed class SessionsConfirmation {
    data class TerminateSession(val session: ActiveSession) : SessionsConfirmation()
    data object TerminateOtherSessions : SessionsConfirmation()
}

sealed class SessionsEffect : Effect() {
    data object ShowTerminateError : SessionsEffect()
}

sealed class SessionsIntent : Intent() {
    data object OnRetryClick : SessionsIntent()
    data class OnTerminateSessionClick(val session: ActiveSession) : SessionsIntent()
    data object OnTerminateOtherSessionsClick : SessionsIntent()
    data object OnConfirmationConfirmed : SessionsIntent()
    data object OnConfirmationDismissed : SessionsIntent()
}
