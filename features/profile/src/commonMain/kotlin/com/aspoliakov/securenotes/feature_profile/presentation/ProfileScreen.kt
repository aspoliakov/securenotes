package com.aspoliakov.securenotes.feature_profile.presentation

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
 * Project SecureNotes
 */

@Composable
fun ProfileScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state = viewModel.currentState
    ProfileScreen(
            modifier = modifier,
            state = state,
            intentHandler = viewModel::handleIntent,
    )
}

@Composable
internal fun ProfileScreen(
        modifier: Modifier = Modifier,
        state: ProfileState = ProfileState.Idle,
        intentHandler: (ProfileIntent) -> Unit = {},
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
                    text = "Profile",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
