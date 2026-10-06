package com.aspoliakov.securenotes.feature_note.presentation.styled_text

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

/** Stateless style buttons. They never take focus, so the keyboard and selection in the editor stay. */
@Composable
internal fun TextStylesToolbar(
        modifier: Modifier = Modifier,
        onStyleClick: (TextStyleAction) -> Unit,
) {
    Row(
            modifier = modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        TextStyleAction.entries.forEach { action ->
            IconButton(
                    modifier = Modifier.focusProperties { canFocus = false },
                    onClick = { onStyleClick(action) },
            ) {
                Icon(
                        imageVector = action.icon,
                        contentDescription = stringResource(action.label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
