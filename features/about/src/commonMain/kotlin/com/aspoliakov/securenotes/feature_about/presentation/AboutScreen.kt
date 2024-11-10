package com.aspoliakov.securenotes.feature_about.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.koin.compose.viewmodel.koinViewModel

/**
 * Project SecureAbouts
 */

@Composable
fun AboutScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<AboutViewModel>()
    val state = viewModel.currentState
    AboutScreen(
            modifier = modifier,
            state = state,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun AboutScreen(
        modifier: Modifier = Modifier,
        state: AboutState = AboutState,
        intentHandler: (AboutIntent) -> Unit = {},
) {
    Scaffold(modifier = modifier) { paddings ->
        Column(
                modifier = modifier
                        .padding(paddings)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
        ) {
            Text(
                    text = "About",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
