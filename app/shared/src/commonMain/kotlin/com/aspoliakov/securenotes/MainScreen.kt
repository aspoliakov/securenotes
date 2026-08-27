package com.aspoliakov.securenotes

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aspoliakov.securenotes.core_presentation.navigation.Screen
import com.aspoliakov.securenotes.feature_about.presentation.AboutScreenRoute
import com.aspoliakov.securenotes.feature_home.notesItem
import com.aspoliakov.securenotes.feature_home.presentation.HomeScreenRoute
import com.aspoliakov.securenotes.feature_home.profileItem
import com.aspoliakov.securenotes.feature_note.presentation.NoteScreenRoute
import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserScreenRoute
import com.aspoliakov.securenotes.feature_profile.presentation.ProfileScreenRoute

/**
 * Project SecureNotes
 */

@Composable
internal fun MainScreen() {
    val navController = rememberNavController()
    NavHost(
            navController = navController,
            startDestination = Screen.Home,
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
        composable<Screen.Home> {
            HomeScreenRoute(
                    modifier = Modifier,
                    navItems = listOf(
                            notesItem {
                                NotesBrowserScreenRoute(
                                        onNavigateToNote = { noteId ->
                                            navController.navigate(Screen.Note(noteId = noteId))
                                        },
                                        onNavigateToCreateNote = {
                                            navController.navigate(Screen.Note())
                                        },
                                )
                            },
                            profileItem {
                                ProfileScreenRoute(
                                        onNavigateToAbout = {
                                            navController.navigate(Screen.About)
                                        }
                                )
                            },
                    ),
            )
        }
        composable<Screen.Note> { entry ->
            val route: Screen.Note = entry.toRoute()
            NoteScreenRoute(
                    noteId = route.noteId,
                    onNavigationBack = { navController.popBackStack() },
            )
        }
        composable<Screen.About> {
            AboutScreenRoute(
                    onNavigationBack = { navController.popBackStack() },
            )
        }
    }
}
