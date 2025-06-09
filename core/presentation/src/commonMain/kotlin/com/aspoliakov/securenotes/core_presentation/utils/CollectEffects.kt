package com.aspoliakov.securenotes.core_presentation.utils

import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

/**
 * Project Express. Created by Unlimited Production team.
 */

@Composable
@Suppress("UNCHECKED_CAST")
fun <E : Effect> CollectEffects(
        effects: Flow<Effect>,
        onEffect: (E) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        effects
                .flowWithLifecycle(lifecycleOwner.lifecycle)
                .collect { effect ->
                    val requiredEffect = effect as? E
                    if (requiredEffect != null) {
                        onEffect(requiredEffect)
                    } else {
                        Napier.e("Unknown effect $effect")
                    }
                }
    }
}
