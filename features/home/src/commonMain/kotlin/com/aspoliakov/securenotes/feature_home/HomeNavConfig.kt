package com.aspoliakov.securenotes.feature_home

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.feature_home_menu_item_notes
import com.aspoliakov.securenotes.core_ui.resources.feature_home_menu_item_profile
import com.aspoliakov.securenotes.core_ui.resources.notes
import com.aspoliakov.securenotes.core_ui.resources.notes_filled
import com.aspoliakov.securenotes.core_ui.resources.profile
import com.aspoliakov.securenotes.core_ui.resources.profile_filled
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Project SecureNotes
 */

data class HomeNavItem(
        val destinationName: String,
        val titleRes: StringResource,
        val iconSelected: DrawableResource,
        val iconUnselected: DrawableResource,
        val content: @Composable () -> Unit,
)

fun notesItem(content: @Composable () -> Unit): HomeNavItem {
    return HomeNavItem(
            destinationName = "notes",
            titleRes = Res.string.feature_home_menu_item_notes,
            iconSelected = Res.drawable.notes_filled,
            iconUnselected = Res.drawable.notes,
            content = content,
    )
}

fun profileItem(content: @Composable () -> Unit): HomeNavItem {
    return HomeNavItem(
            destinationName = "profile",
            titleRes = Res.string.feature_home_menu_item_profile,
            iconSelected = Res.drawable.profile_filled,
            iconUnselected = Res.drawable.profile,
            content = content,
    )
}
