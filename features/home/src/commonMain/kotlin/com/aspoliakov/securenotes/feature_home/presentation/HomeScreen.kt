package com.aspoliakov.securenotes.feature_home.presentation

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aspoliakov.securenotes.feature_home.HomeNavItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
fun HomeScreenRoute(
        modifier: Modifier = Modifier,
        navItems: List<HomeNavItem>,
) {
    HomeScreen(
            modifier = modifier,
            navItems = navItems,
    )
}

@Composable
internal fun HomeScreen(
        modifier: Modifier = Modifier,
        navItems: List<HomeNavItem>,
) {
    val navController = rememberNavController()
    Scaffold(
            bottomBar = {
                BottomNavigationMenu(
                        navController = navController,
                        navItems = navItems,
                )
            },
    ) { innerPadding ->
        Box(
                modifier = modifier.padding(innerPadding),
        ) {
            NavHost(
                    startDestination = navItems.first().destinationName,
                    navController = navController,
                    modifier = modifier.fillMaxSize(),
            ) {
                navItems.forEach { navItem ->
                    composable(route = navItem.destinationName) {
                        navItem.content()
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationMenu(
        navController: NavController,
        navItems: List<HomeNavItem>,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp,
    ) {
        navItems.forEach { navItem ->
            NavigationBarItem(
                    label = {
                        Text(text = stringResource(navItem.titleRes))
                    },
                    selected = navItem.destinationName == currentRoute,
                    icon = {
                        Icon(
                                painter = painterResource(
                                        if (navItem.destinationName == currentRoute) {
                                            navItem.iconSelected
                                        } else {
                                            navItem.iconUnselected
                                        }
                                ),
                                contentDescription = stringResource(navItem.titleRes),
                        )
                    },
                    onClick = {
                        if (navItem.destinationName != currentRoute) {
                            navController.navigate(navItem.destinationName) {
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
