package com.aspoliakov.securenotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.core_presentation.navigation.Screen
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.app_name
import com.aspoliakov.securenotes.feature_auth.presentation.AuthScreenRoute
import com.aspoliakov.securenotes.feature_home.presentation.HomeScreenRoute
import com.aspoliakov.securenotes.ui.MainViewModel
import com.aspoliakov.securenotes.ui.MainViewState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Project SecureNotes
 */

@Composable
fun MainAppComposable() {
    val navController = rememberNavController()
    AppTheme {
        MainAppNavHost(
                navController = navController,
        )
    }
}

@Composable
internal fun MainAppNavHost(
        navController: NavHostController,
) {
    val mainViewModel = koinInject<MainViewModel>()
    val navigateToScreen: (screen: Screen) -> Unit = { navController.navigate(it.route) }
    NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) { LoadingScreen() }
        composable(Screen.Auth.route) { AuthScreenRoute(viewModel = koinInject()) }
        composable(Screen.Home.route) { HomeScreenRoute(viewModel = koinInject()) }
    }
    val state = mainViewModel.currentState
    navController.popBackStack()
    navController.navigate(
            when (state) {
                is MainViewState.Loading -> Screen.Splash.route
                is MainViewState.Unauthorized -> Screen.Auth.route
                is MainViewState.Authorized -> Screen.Home.route
            }
    )
}

@Composable
internal fun LoadingScreen(
        modifier: Modifier = Modifier,
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
                    text = stringResource(Res.string.app_name),
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
