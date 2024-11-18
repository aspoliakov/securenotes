package com.aspoliakov.securenotes

import androidx.compose.runtime.Composable
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
    NavHost(
            navController = navController,
            startDestination = AppGlobalScreen.Splash.toString(),
    ) {
        composable(AppGlobalScreen.Splash.toString()) { SplashScreen() }
        composable(AppGlobalScreen.Auth.toString()) { AuthScreenRoute() }
        composable(AppGlobalScreen.Keys.toString()) { KeysScreenRoute() }
        composable(AppGlobalScreen.Main.toString()) { MainScreen() }
    }
    val state = mainViewModel.currentState
    navController.popBackStack()
    navController.navigate(
            when (state) {
                is AppComposableState.Loading -> AppGlobalScreen.Splash.toString()
                is AppComposableState.Unauthorized -> AppGlobalScreen.Auth.toString()
                is AppComposableState.Authorized -> AppGlobalScreen.Keys.toString()
                is AppComposableState.Active -> AppGlobalScreen.Main.toString()
            }
    )
}
