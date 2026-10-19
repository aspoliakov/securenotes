package com.aspoliakov.securenotes.feature_sessions.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_user_state.ActiveSessionsInteractor

/**
 * Project SecureNotes
 */

class SessionsViewModel(
        initialState: SessionsState,
        private val activeSessionsInteractor: ActiveSessionsInteractor,
) : MviViewModel<SessionsState, SessionsEffect, SessionsIntent>(initialState) {

    init {
        loadSessions()
    }

    override fun handleIntent(intent: SessionsIntent) {
        when (intent) {
            is SessionsIntent.OnRetryClick -> loadSessions()
            is SessionsIntent.OnTerminateSessionClick -> showConfirmation(
                    SessionsConfirmation.TerminateSession(intent.session)
            )
            is SessionsIntent.OnTerminateOtherSessionsClick -> showConfirmation(
                    SessionsConfirmation.TerminateOtherSessions
            )
            is SessionsIntent.OnConfirmationConfirmed -> onConfirmationConfirmed()
            is SessionsIntent.OnConfirmationDismissed -> reduceState { copy(confirmation = null) }
        }
    }

    private fun loadSessions() {
        reduceState { copy(contentState = SessionsContentState.Loading) }
        launchOnIO {
            activeSessionsInteractor.getSessions()
                .onSuccess { sessions ->
                    reduceState {
                        copy(
                                contentState = SessionsContentState.Loaded(
                                        currentSession = sessions.firstOrNull { it.isCurrent },
                                        otherSessions = sessions.filterNot { it.isCurrent },
                                )
                        )
                    }
                }
                .onFailure {
                    reduceState { copy(contentState = SessionsContentState.Error) }
                }
        }
    }

    private fun showConfirmation(confirmation: SessionsConfirmation) {
        if (currentState.isTerminating) return
        reduceState { copy(confirmation = confirmation) }
    }

    private fun onConfirmationConfirmed() {
        val confirmation = currentState.confirmation ?: return
        reduceState {
            copy(
                    confirmation = null,
                    isTerminating = true,
            )
        }
        launchOnIO {
            val result = when (confirmation) {
                is SessionsConfirmation.TerminateSession ->
                    activeSessionsInteractor.terminateSession(confirmation.session.id)
                is SessionsConfirmation.TerminateOtherSessions ->
                    activeSessionsInteractor.terminateOtherSessions()
            }
            result
                .onSuccess {
                    reduceState { copy(contentState = contentState.withoutTerminated(confirmation)) }
                }
                .onFailure {
                    sendEffect { SessionsEffect.ShowTerminateError }
                }
            reduceState { copy(isTerminating = false) }
        }
    }

    private fun SessionsContentState.withoutTerminated(
            confirmation: SessionsConfirmation,
    ): SessionsContentState {
        return if (this !is SessionsContentState.Loaded) {
            this
        } else {
            val remaining = when (confirmation) {
                is SessionsConfirmation.TerminateSession -> otherSessions.filterNot {
                    it.id == confirmation.session.id
                }
                is SessionsConfirmation.TerminateOtherSessions -> emptyList()
            }
            copy(otherSessions = remaining)
        }
    }
}
