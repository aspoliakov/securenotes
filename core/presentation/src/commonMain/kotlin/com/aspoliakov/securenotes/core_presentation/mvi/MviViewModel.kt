package com.aspoliakov.securenotes.core_presentation.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Project SecureNotes
 */

abstract class MviViewModel<S : MviState, E : MviEffect, I : MviIntent>(initialState: S) : ViewModel() {

    var currentState by mutableStateOf(initialState)
        private set

    private val _intent = MutableSharedFlow<I>()
    val intent = _intent.asSharedFlow()

    private val _effects = Channel<MviEffect>()
    val effects = _effects.receiveAsFlow()

    private val changeStateLock = SynchronizedObject()

    init {
        viewModelScope.launch {
            intent.collect {
                handleIntent(it)
            }
        }
    }

    fun emitIntent(intent: I) {
        viewModelScope.launch { _intent.emit(intent) }
    }

    protected abstract fun handleIntent(intent: I)

    protected fun reduceState(reduce: S.() -> S) {
        synchronized(changeStateLock) {
            this.currentState = currentState.reduce()
        }
    }

    protected fun <E : MviEffect> sendEffect(builder: () -> E) {
        viewModelScope.launch { _effects.send(builder()) }
    }
}

open class MviState
open class MviEffect
open class MviIntent

sealed class CommonEffect : MviEffect() {
    data class ShowError(
            val resId: Int,
            val errorBody: ErrorBody,
    ) : CommonEffect() {
        sealed class ErrorBody {
            data class Cause(val cause: Throwable) : ErrorBody()
            data class Message(val message: String) : ErrorBody()
        }
    }

//    data class ShowToast(
//            @StringRes val resId: Int,
//            val duration: Int = Toast.LENGTH_SHORT,
//    ) : CommonEffect()
}

data class StateChange<S>(
        val newState: S,
        val oldState: S?,
)