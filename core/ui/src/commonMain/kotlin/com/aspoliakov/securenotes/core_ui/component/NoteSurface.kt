package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

/**
 * Project SecureNotes
 */

val NoteShape = RoundedCornerShape(20.dp)

private const val NOTE_COLOR_TINT_ALPHA = 0.35F

@Composable
fun noteContainerColor(argb: Long?): Color {
    val surface = MaterialTheme.colorScheme.surfaceContainerLow
    return if (argb == null) surface else Color(argb).copy(alpha = NOTE_COLOR_TINT_ALPHA).compositeOver(surface)
}
