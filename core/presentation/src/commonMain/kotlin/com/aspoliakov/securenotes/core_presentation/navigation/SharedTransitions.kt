package com.aspoliakov.securenotes.core_presentation.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.aspoliakov.securenotes.core_ui.component.NoteShape

/**
 * Project SecureNotes
 */

/** Scope of the [androidx.compose.animation.SharedTransitionLayout] wrapping the NavHost. Null in previews. */
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

/** Enter/exit scope of the current NavHost destination. Null in previews and dialogs. */
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

private const val SCREEN_CHROME_OVERLAY_Z_INDEX = 1F

private val EmptyShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Rectangle(Rect.Zero)
    }
}

/**
 * Container transform of a note card: the card in the notes browser and the card on the note screen share bounds,
 * so opening a note looks like the card expanding to the whole screen. No-op without a nav transition.
 */
@Composable
fun Modifier.sharedNoteBounds(
        noteId: String?,
        resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val visibilityScope = LocalNavAnimatedVisibilityScope.current
    if (noteId == null || sharedTransitionScope == null || visibilityScope == null) return this
    return with(sharedTransitionScope) {
        sharedBounds(
                sharedContentState = rememberSharedContentState(key = "note_card_$noteId"),
                animatedVisibilityScope = visibilityScope,
                resizeMode = resizeMode,
                clipInOverlayDuringTransition = OverlayClip(NoteShape),
        )
    }
}

/**
 * How much of the destination's nav transition is still ahead: 1 before it enters and after it exits, 0 while it
 * is shown, animated in between along with the [sharedNoteBounds] transform. Always 0 without a nav transition.
 */
@Composable
fun rememberNavTransitionFraction(): () -> Float {
    val visibilityScope = LocalNavAnimatedVisibilityScope.current ?: return { 0F }
    val fraction = visibilityScope.transition.animateFloat(
            transitionSpec = { spring(stiffness = Spring.StiffnessMediumLow) },
            label = "nav_transition_fraction",
    ) { state ->
        if (state == EnterExitState.Visible) 0F else 1F
    }
    return remember(fraction) { { fraction.value } }
}

/**
 * Animates screen chrome (bars, floating buttons) together with the screen's nav transition. While a shared
 * element transition runs the chrome is drawn above the shared elements, so an expanding card doesn't cover it.
 * No-op without a nav transition.
 */
@Composable
fun Modifier.screenChromeTransition(
        enter: EnterTransition,
        exit: ExitTransition,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val visibilityScope = LocalNavAnimatedVisibilityScope.current ?: return this
    val overlayModifier = if (sharedTransitionScope != null) {
        with(sharedTransitionScope) {
            Modifier.renderInSharedTransitionScopeOverlay(zIndexInOverlay = SCREEN_CHROME_OVERLAY_Z_INDEX)
        }
    } else {
        Modifier
    }
    return with(visibilityScope) {
        then(overlayModifier).animateEnterExit(enter = enter, exit = exit)
    }
}

/**
 * A popped destination stays above the destination below until its exit transition ends, so it would swallow
 * taps meant for the screen that is already visible underneath. While the destination exits with an active
 * shared element transition, this clips it to nothing, which makes it transparent to hit testing.
 * Apply only to a screen whose visible parts render in the shared transition overlay during the exit
 * ([sharedNoteBounds], [screenChromeTransition]): anything else in it isn't drawn during that time.
 */
@Composable
fun Modifier.passInputThroughWhileExiting(): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val visibilityScope = LocalNavAnimatedVisibilityScope.current
    if (sharedTransitionScope == null || visibilityScope == null) return this
    return graphicsLayer {
        val isExiting = visibilityScope.transition.targetState == EnterExitState.PostExit
        clip = isExiting && sharedTransitionScope.isTransitionActive
        shape = EmptyShape
    }
}
