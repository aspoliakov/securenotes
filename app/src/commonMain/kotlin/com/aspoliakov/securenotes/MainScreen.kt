package com.aspoliakov.securenotes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.core_presentation.navigation.MainNavigation
import com.aspoliakov.securenotes.feature_home.presentation.HomeScreenRoute

/**
 * Project Express. Created by Unlimited Production team.
 */

@Composable
internal fun MainScreen() {
    val navController = rememberNavController()
    NavHost(
            navController = navController,
            startDestination = MainNavigation.Home.route,
    ) {
        composable(MainNavigation.Home.route) { HomeScreenRoute() }
        composable(MainNavigation.Note.route) { SplashScreen() }
    }
}
