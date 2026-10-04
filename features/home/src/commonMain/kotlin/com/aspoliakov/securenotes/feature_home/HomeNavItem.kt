package com.aspoliakov.securenotes.feature_home

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.feature_home_menu_item_notes
import com.aspoliakov.securenotes.core_ui.resources.feature_home_menu_item_profile
import com.aspoliakov.securenotes.core_ui.resources.ic_notes
import com.aspoliakov.securenotes.core_ui.resources.ic_notes_filled
import com.aspoliakov.securenotes.core_ui.resources.ic_profile
import com.aspoliakov.securenotes.core_ui.resources.ic_profile_filled
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Project SecureNotes
 */

data class HomeNavItem(
        val route: HomeTab,
        val titleRes: StringResource,
        val iconSelected: DrawableResource,
        val iconUnselected: DrawableResource,
        val content: @Composable () -> Unit,
)

fun notesItem(content: @Composable () -> Unit): HomeNavItem {
    return HomeNavItem(
            route = HomeTab.Notes,
            titleRes = Res.string.feature_home_menu_item_notes,
            iconSelected = Res.drawable.ic_notes_filled,
            iconUnselected = Res.drawable.ic_notes,
            content = content,
    )
}

fun profileItem(content: @Composable () -> Unit): HomeNavItem {
    return HomeNavItem(
            route = HomeTab.Profile,
            titleRes = Res.string.feature_home_menu_item_profile,
            iconSelected = Res.drawable.ic_profile_filled,
            iconUnselected = Res.drawable.ic_profile,
            content = content,
    )
}
