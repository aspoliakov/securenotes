package com.aspoliakov.securenotes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.core_presentation.navigation.AppGlobalScreen
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.feature_auth.presentation.AuthScreenRoute
import com.aspoliakov.securenotes.feature_keys.presentation.KeysScreenRoute
import org.koin.compose.viewmodel.koinViewModel

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
    val mainViewModel = koinViewModel<AppComposableViewModel>()
    val startDestination = remember { mainViewModel.currentState.toScreen() }
    NavHost(
            navController = navController,
            startDestination = startDestination,
    ) {
        composable(AppGlobalScreen.Auth.toString()) { AuthScreenRoute() }
        composable(AppGlobalScreen.Keys.toString()) { KeysScreenRoute() }
        composable(AppGlobalScreen.Main.toString()) { MainScreen() }
    }
    val destination = mainViewModel.currentState.toScreen()
    if (navController.currentDestination?.route != destination) {
        navController.popBackStack()
        navController.navigate(destination)
    }
}

fun AppComposableState.toScreen(): String {
    return when (this) {
        is AppComposableState.Unauthorized -> AppGlobalScreen.Auth
        is AppComposableState.Authorized -> AppGlobalScreen.Keys
        is AppComposableState.Active -> AppGlobalScreen.Main
    }.toString()
}
