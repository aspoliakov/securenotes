package com.aspoliakov.securenotes

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.navigation.AppGlobalScreen
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.feature_auth.presentation.AuthScreenRoute
import com.aspoliakov.securenotes.feature_keys.presentation.KeysScreenRoute

/**
 * Project SecureNotes
 */

@Composable
fun MainAppComposable() {
    val navController = rememberNavController()
    AppTheme {
        val mainViewModel = koinMviViewModel<AppComposableViewModel>()
        MainAppNavHost(
                state = mainViewModel.currentState,
                navController = navController,
        )
    }
}

@Composable
internal fun MainAppNavHost(
        state: AppComposableState,
        navController: NavHostController,
) {
    val destination = remember(state) { state.toScreen() }
    val startDestination = remember { destination }
    var initialized by remember { mutableStateOf(false) }
    NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(durationMillis = 300),
                )
            },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = {
                slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(durationMillis = 300),
                )
            },
    ) {
        composable(AppGlobalScreen.Auth.toString()) { AuthScreenRoute() }
        composable(AppGlobalScreen.Keys.toString()) { KeysScreenRoute() }
        composable(AppGlobalScreen.Main.toString()) { MainScreen() }
    }
    LaunchedEffect(destination) {
        if (initialized) {
            navController.navigate(destination) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
                restoreState = false
            }
        } else {
            initialized = true
        }
    }
}

fun AppComposableState.toScreen(): String {
    return when (this) {
        is AppComposableState.Unauthorized -> AppGlobalScreen.Auth
        is AppComposableState.Authorized -> AppGlobalScreen.Keys
        is AppComposableState.Active -> AppGlobalScreen.Main
    }.toString()
}
