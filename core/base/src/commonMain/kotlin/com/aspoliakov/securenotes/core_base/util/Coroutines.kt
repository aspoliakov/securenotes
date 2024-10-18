package com.aspoliakov.securenotes.core_base.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

/**
 * Project SecureNotes
 */

fun <T> Flow<T>.flowOnIO() = flowOn(Dispatchers.IO)

fun <T> Flow<T>.flowOnMain() = flowOn(Dispatchers.Main)

@Suppress("FunctionName")
fun IOScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
