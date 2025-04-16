package com.aspoliakov.securenotes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
            startDestination = Screen.Home.toString(),
    ) {
        composable(Screen.Home.toString()) {
            HomeScreenRoute(
                    modifier = Modifier,
                    navItems = listOf(
                            notesItem {
                                NotesBrowserScreenRoute(
                                        onNavigateToNote = { noteId ->
                                            navController.navigate("${Screen.Note}/${noteId}")
                                        },
                                        onNavigateToCreateNote = {
                                            navController.navigate("${Screen.Note}/null")
                                        },
                                )
                            },
                            profileItem {
                                ProfileScreenRoute(
                                        onNavigateToAbout = {
                                            navController.navigate("${Screen.About}")
                                        }
                                )
                            },
                    ),
            )
        }
        composable(
                route = "${Screen.Note}/{${Screen.Note.ARG_NOTE_ID}}",
                arguments = listOf(
                        navArgument(Screen.Note.ARG_NOTE_ID) {
                            type = NavType.StringType
                            nullable = true
                        },
                )
        ) { entry ->
            NoteScreenRoute(
                    noteId = entry.arguments?.getString(Screen.Note.ARG_NOTE_ID),
                    onNavigationBack = { navController.popBackStack() },
            )
        }
        composable(Screen.About.toString()) {
            AboutScreenRoute()
        }
    }
}
