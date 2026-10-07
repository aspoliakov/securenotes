package com.aspoliakov.securenotes

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.navigation.AppGlobalScreen
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.domain_user_state.model.AppThemeMode
import com.aspoliakov.securenotes.feature_auth.presentation.AuthScreenRoute
import com.aspoliakov.securenotes.feature_keys.presentation.KeysScreenRoute

/**
 * Project SecureNotes
 */

@Composable
fun MainAppComposable() {
    val navController = rememberNavController()
    val mainViewModel = koinMviViewModel<AppComposableViewModel>()
    val state by mainViewModel.state.collectAsState()
    val darkTheme = state.themeMode.isDarkTheme()
    SystemBarsEffect(darkTheme = darkTheme)
    AppTheme(darkTheme = darkTheme) {
        MainAppNavHost(
                globalState = state.globalState,
                navController = navController,
        )
    }
}

@Composable
private fun AppThemeMode.isDarkTheme(): Boolean {
    return when (this) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
}

@Composable
internal fun MainAppNavHost(
        globalState: AppGlobalState,
        navController: NavHostController,
) {
    val destination = remember(globalState) { globalState.toScreen() }
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
        composable<AppGlobalScreen.Auth> { AuthScreenRoute() }
        composable<AppGlobalScreen.Keys> { KeysScreenRoute() }
        composable<AppGlobalScreen.Main> { MainScreen() }
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

fun AppGlobalState.toScreen(): AppGlobalScreen {
    return when (this) {
        is AppGlobalState.Auth -> AppGlobalScreen.Auth
        is AppGlobalState.Keys -> AppGlobalScreen.Keys
        is AppGlobalState.Active -> AppGlobalScreen.Main
    }
}
