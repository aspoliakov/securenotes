package com.aspoliakov.securenotes.feature_home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.feature_home_menu_item_notes
import com.aspoliakov.securenotes.core_ui.resources.feature_home_menu_item_profile
import com.aspoliakov.securenotes.core_ui.resources.notes
import com.aspoliakov.securenotes.core_ui.resources.notes_filled
import com.aspoliakov.securenotes.core_ui.resources.profile
import com.aspoliakov.securenotes.core_ui.resources.profile_filled
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Project SecureNotes
 */

@Composable
fun HomeScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = koinInject()
    val state = viewModel.currentState
    HomeScreen(
            modifier = modifier,
            state = state,
            intentHandler = viewModel::handleIntent,
    )
}

@Composable
internal fun HomeScreen(
        modifier: Modifier = Modifier,
        state: HomeState = HomeState.Idle,
        intentHandler: (HomeIntent) -> Unit = {},
) {
    val navController = rememberNavController()
    Scaffold(
            bottomBar = {
                BottomNavigationMenu(navController = navController)
            },
    ) { innerPadding ->
        Box(
                modifier = modifier.padding(innerPadding),
        ) {
            NavHost(
                    startDestination = HomeNavigation.Notes.route,
                    navController = navController,
                    modifier = modifier.fillMaxSize(),
            ) {
                composable(route = HomeNavigation.Notes.route) {
                    StubScreen(
                            stubText = HomeNavigation.Notes.titleRes,
                    )
                }
                composable(route = HomeNavigation.Profile.route) {
                    StubScreen(
                            stubText = HomeNavigation.Profile.titleRes,
                    )
                }
            }
        }
    }
}

@Composable
internal fun StubScreen(
        modifier: Modifier = Modifier,
        stubText: StringResource,
) {
    Scaffold(modifier = modifier) { paddings ->
        Column(
                modifier = modifier
                        .padding(paddings)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
        ) {
            Text(
                    text = stringResource(stubText),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun BottomNavigationMenu(
        navController: NavController,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp,
    ) {
        listOf(
                HomeNavigation.Notes,
                HomeNavigation.Profile,
        ).forEach {
            NavigationBarItem(
                    label = {
                        Text(text = stringResource(it.titleRes))
                    },
                    selected = it.route == currentRoute,
                    icon = {
                        Icon(
                                painter = painterResource(
                                        if (it.route == currentRoute) {
                                            it.iconSelected
                                        } else {
                                            it.iconUnselected
                                        }
                                ),
                                contentDescription = stringResource(it.titleRes),
                        )
                    },
                    onClick = {
                        if (currentRoute != it.route) {
                            navController.navigate(it.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
            )
        }
    }
}

enum class HomeNavigation(
        val route: String,
        val titleRes: StringResource,
        val iconSelected: DrawableResource,
        val iconUnselected: DrawableResource,
) {
    Notes(
            route = "notes",
            titleRes = Res.string.feature_home_menu_item_notes,
            iconSelected = Res.drawable.notes_filled,
            iconUnselected = Res.drawable.notes,
    ),
    Profile(
            route = "profile",
            titleRes = Res.string.feature_home_menu_item_profile,
            iconSelected = Res.drawable.profile_filled,
            iconUnselected = Res.drawable.profile,
    ),
}

