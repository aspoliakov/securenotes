package com.aspoliakov.securenotes.feature_home.presentation

import androidx.compose.foundation.clickable
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

/**
 * Project SecureNotes
 */

@Composable
fun HomeScreenRoute(
        modifier: Modifier = Modifier,
        viewModel: HomeViewModel,
) {
    val state = viewModel.currentState
    HomeScreen(
            modifier = modifier,
            state = state,
            intentHandler = viewModel::handleIntent,
    )
}

@Composable
internal fun HomeScreen(
        modifier: Modifier = Modifier,
        state: HomeState = HomeState.Idle,
        intentHandler: (HomeIntent) -> Unit = {},
) {
    Scaffold(modifier = modifier) { paddings ->
        Column(
                modifier = modifier
                        .padding(paddings)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(
                    modifier = modifier
                            .clickable { intentHandler.invoke(HomeIntent.AddRandomNote) },
                    text = "HOME",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
