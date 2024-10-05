package com.aspoliakov.securenotes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.secureAbouts.feature_about.presentation.AboutScreenRoute
import com.aspoliakov.securenotes.core_presentation.navigation.Screen
import com.aspoliakov.securenotes.feature_home.presentation.HomeScreenRoute
import com.aspoliakov.securenotes.feature_note.presentation.NoteScreenRoute
import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserScreenRoute
import com.aspoliakov.securenotes.feature_profile.presentation.ProfileScreenRoute

/**
 * Project Express. Created by Unlimited Production team.
 */

@Composable
internal fun MainScreen() {
    val navController = rememberNavController()
    NavHost(
            navController = navController,
            startDestination = Screen.HOME.name,
    ) {
        composable(Screen.HOME.name) {
            HomeScreenRoute(
                    notesContent = {
                        NotesBrowserScreenRoute()
                    },
                    profileContent = {
                        ProfileScreenRoute()
                    },
            )
        }
        composable(Screen.NOTE.name) {
            NoteScreenRoute(noteId = "stub!")
        }
        composable(Screen.ABOUT.name) {
            AboutScreenRoute()
        }
    }
}
