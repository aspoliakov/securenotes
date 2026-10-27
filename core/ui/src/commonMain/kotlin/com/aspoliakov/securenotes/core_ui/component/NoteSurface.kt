package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Project SecureNotes
 */

val NoteShape = RoundedCornerShape(20.dp)

private const val NOTE_COLOR_TINT_ALPHA = 0.35F
private val HiddenNoteBlurRadius = 12.dp

@Composable
fun noteContainerColor(argb: Long?): Color {
    val surface = MaterialTheme.colorScheme.surfaceContainerLow
    return if (argb == null) surface else Color(argb).copy(alpha = NOTE_COLOR_TINT_ALPHA).compositeOver(surface)
}

/**
 * Hides the content of a hidden note: blurs it by [amount] (0 is clear, 1 is fully hidden), read on every frame,
 * so it can follow an animation without recomposition. Where blur isn't drawn (Android before 12) the content
 * is covered with [containerColor] instead.
 */
fun Modifier.hiddenNoteContent(
        containerColor: Color,
        amount: () -> Float,
): Modifier {
    return if (isBlurSupported) {
        graphicsLayer {
            val radius = HiddenNoteBlurRadius.toPx() * amount()
            renderEffect = if (radius > 0F) BlurEffect(radius, radius, TileMode.Decal) else null
        }
    } else {
        drawWithContent {
            drawContent()
            drawRect(color = containerColor, alpha = amount())
        }
    }
}
