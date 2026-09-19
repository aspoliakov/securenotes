package com.aspoliakov.securenotes.feature_home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.feature_home.HomeNavItem
import com.aspoliakov.securenotes.feature_home.notesItem
import com.aspoliakov.securenotes.feature_home.profileItem
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
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val saveableStateHolder = rememberSaveableStateHolder()
    Scaffold(
            bottomBar = {
                BottomNavigationMenu(
                        navItems = navItems,
                        selectedIndex = selectedIndex,
                        onSelect = { selectedIndex = it },
                )
            },
    ) { innerPadding ->
        Box(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            navItems.forEachIndexed { index, navItem ->
                if (index == selectedIndex) {
                    saveableStateHolder.SaveableStateProvider(key = index) {
                        navItem.content()
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationMenu(
        navItems: List<HomeNavItem>,
        selectedIndex: Int,
        onSelect: (Int) -> Unit,
) {
    NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp,
    ) {
        navItems.forEachIndexed { index, navItem ->
            val isSelected = index == selectedIndex
            NavigationBarItem(
                    label = {
                        Text(text = stringResource(navItem.titleRes))
                    },
                    selected = isSelected,
                    icon = {
                        Icon(
                                painter = painterResource(
                                        if (isSelected) {
                                            navItem.iconSelected
                                        } else {
                                            navItem.iconUnselected
                                        }
                                ),
                                contentDescription = stringResource(navItem.titleRes),
                        )
                    },
                    onClick = { onSelect(index) },
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
                navItems = listOf(
                        notesItem { Text(text = "Notes") },
                        profileItem { Text(text = "Profile") },
                ),
        )
    }
}
