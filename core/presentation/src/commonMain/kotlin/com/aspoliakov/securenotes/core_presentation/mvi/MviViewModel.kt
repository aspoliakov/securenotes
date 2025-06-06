package com.aspoliakov.securenotes.core_presentation.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersDefinition

/**
 * Project SecureNotes
 */

abstract class MviViewModel<S : State, E : Effect, I : Intent>(initialState: S) : ViewModel() {

    var currentState by mutableStateOf(initialState)
        private set

    private val _intent = MutableSharedFlow<I>()
    val intent = _intent.asSharedFlow()

    private val _effects = Channel<Effect>()
    val effects = _effects.receiveAsFlow()

    private val changeStateLock = SynchronizedObject()

    init {
        viewModelScope.launch {
            intent.collect {
                handleIntent(it)
            }
        }
    }

    open fun initData() {
        // initial loading
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

    protected fun <E : Effect> sendEffect(builder: () -> E) {
        viewModelScope.launch { _effects.send(builder()) }
    }
}

@Composable
inline fun <reified T : MviViewModel<*, *, *>> koinMviViewModel(
        noinline parameters: ParametersDefinition? = null,
): T {
    val mviViewModel = koinViewModel<T>(parameters = parameters)
    LaunchedEffect(Unit) {
        mviViewModel.initData()
    }
    return mviViewModel
}

open class State
open class Effect
open class Intent
