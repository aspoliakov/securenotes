package com.aspoliakov.securenotes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.core_presentation.navigation.AppGlobalNavigation
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.feature_auth.presentation.AuthScreenRoute
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
    val mainViewModel = koinInject<AppComposableViewModel>()
    NavHost(
            navController = navController,
            startDestination = AppGlobalNavigation.Splash.route,
    ) {
        composable(AppGlobalNavigation.Splash.route) { SplashScreen() }
        composable(AppGlobalNavigation.Auth.route) { AuthScreenRoute() }
        composable(AppGlobalNavigation.Main.route) { MainScreen() }
    }
    val state = mainViewModel.currentState
    navController.popBackStack()
    navController.navigate(
            when (state) {
                is AppComposableState.Loading -> AppGlobalNavigation.Splash.route
                is AppComposableState.Unauthorized -> AppGlobalNavigation.Auth.route
                is AppComposableState.Authorized -> AppGlobalNavigation.Main.route
            }
    )
}
