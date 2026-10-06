package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Project SecureNotes
 */

private val FloatingButtonGroupHeight = 56.dp

private val FloatingButtonGroupShape = RoundedCornerShape(16.dp)

/**
 * Container for a group of icon buttons in screen toolbars, styled like the standalone toolbar buttons
 * of the notes browser. Animates its size, so buttons can be added, removed or swapped without jumps.
 */
@Composable
fun FloatingButtonGroup(
        modifier: Modifier = Modifier,
        content: @Composable RowScope.() -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        Row(
                modifier = modifier
                    .height(FloatingButtonGroupHeight)
                    .clip(FloatingButtonGroupShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .animateContentSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
        )
    }
}
