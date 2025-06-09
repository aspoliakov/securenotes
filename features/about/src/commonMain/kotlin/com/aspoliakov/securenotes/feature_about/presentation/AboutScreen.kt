package com.aspoliakov.securenotes.feature_about.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Project SecureAbouts
 */

@Composable
fun AboutScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel = koinMviViewModel<AboutViewModel>()
    val state by viewModel.state.collectAsState()
    AboutScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun AboutScreen(
        modifier: Modifier = Modifier,
        state: AboutState = AboutState(),
        effects: Flow<Effect> = emptyFlow(),
        intentHandler: (AboutIntent) -> Unit = {},
) {
    CollectEffects<AboutEffect>(effects) { effect ->
        when (effect) {
            is AboutEffect.ShowSnackbar -> {}
        }
    }
    Scaffold(modifier = modifier) { paddings ->
        Column(
                modifier = modifier
                        .padding(paddings)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
        ) {
            Text(
                    modifier = Modifier.clickable {
                        intentHandler.invoke(AboutIntent)
                    },
                    text = state.appVersion,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
