package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import kotlin.math.abs
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

/**
 * Project SecureNotes
 */

/**
 * Per-list/grid state for a single drag-to-reorder gesture, generic over item type [T] via a
 * stable key ([keyOf]) — should match whatever key the Lazy list/grid itself uses. One instance is
 * shared by every item's [dragReorderModifier] call for that list — that's why [draggingKey] and
 * [displayedItems] are top-level state here rather than local to a single item's gesture.
 *
 * [displayedItems] is a live preview shown only while dragging; [dragReorderModifier] reorders it
 * as the drag crosses slots, then reports the final index via its `onReordered` callback and calls
 * [endDrag] — after that the caller's own (now-persisted) list takes over rendering again.
 * [currentDraggingIndex] always resolves against this preview, since it's the only place with the
 * live order while a drag is in progress.
 *
 * [desiredPosition] = [draggingItemStartOffset] + [dragOffset], in the same viewport-relative space
 * the Lazy layout uses for item offsets. [dragReorderModifier] diffs this against the item's live
 * natural offset each frame to compute the drawn translation.
 */
@Stable
class DragReorderState<T>(private val keyOf: (T) -> Any) {

    var draggingKey by mutableStateOf<Any?>(null)
        private set
    var displayedItems by mutableStateOf<List<T>>(emptyList())
        private set
    var dragOffset by mutableStateOf(Offset.Zero)
        private set
    var draggingItemStartOffset by mutableStateOf(Offset.Zero)
        private set

    fun key(item: T): Any {
        return keyOf(item)
    }

    fun isDragging(item: T): Boolean {
        return draggingKey != null && draggingKey == keyOf(item)
    }

    /**
     * Snapshots [items] into [displayedItems] so the drag can reorder that copy freely — via
     *  [moveItem] — without touching the caller's real list until the gesture actually ends.
     */
    fun startDrag(
            item: T,
            items: List<T>,
            startOffset: Offset,
    ) {
        draggingKey = keyOf(item)
        displayedItems = items
        dragOffset = Offset.Zero
        draggingItemStartOffset = startOffset
    }

    fun accumulateDrag(delta: Offset) {
        dragOffset += delta
    }

    fun desiredPosition(): Offset {
        return draggingItemStartOffset + dragOffset
    }

    /**
     * Auto-scroll shifts every item's viewport-relative offset without any pointer movement, so
     * [draggingItemStartOffset] needs the same shift to keep [desiredPosition] pointing at the same
     * absolute spot — otherwise the item drifts from the finger while auto-scrolling.
     */
    fun adjustForScroll(consumedScrollY: Float) {
        draggingItemStartOffset = draggingItemStartOffset.copy(y = draggingItemStartOffset.y - consumedScrollY)
    }

    fun currentDraggingIndex(): Int {
        return displayedItems.indexOfFirst { keyOf(it) == draggingKey }
    }

    fun moveItem(from: Int, to: Int) {
        if (from < 0 || to < 0 || from == to) return
        displayedItems = displayedItems.toMutableList().apply { add(to, removeAt(from)) }
    }

    fun endDrag() {
        draggingKey = null
        dragOffset = Offset.Zero
    }
}

/**
 * Nearest-slot hit test for a 1D list, run on every drag movement and auto-scroll tick to decide
 * whether [DragReorderState.moveItem] should fire.
 *
 * The dragged item's own slot is included as a candidate, not excluded — that's what makes this a
 * threshold (a neighbor only wins once the desired center passes the midpoint between the two
 * slots) instead of always snapping to whichever neighbor is nearest, which would read as the item
 * jumping the instant it's picked up.
 */
fun <T> findTargetIndexInList(
        listState: LazyListState,
        dragState: DragReorderState<T>,
): Int? {
    val draggingKey = dragState.draggingKey ?: return null
    val visibleItems = listState.layoutInfo.visibleItemsInfo
    return visibleItems.find { it.key == draggingKey }?.let { draggedInfo ->
        val desiredCenter = dragState.desiredPosition().y + draggedInfo.size / 2f
        val targetIndex = visibleItems
            .minByOrNull { info -> abs((info.offset + info.size / 2f) - desiredCenter) }
            ?.index
        // LazyColumn keeps the first-visible item anchored at the same scroll offset across
        // recomposition; that fights a reorder that makes a different item become index 0, so we
        // explicitly re-pin the scroll position whenever index 0 is involved in the swap.
        if (targetIndex != null &&
                (draggedInfo.index == listState.firstVisibleItemIndex || targetIndex == listState.firstVisibleItemIndex)
        ) {
            listState.requestScrollToItem(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
        targetIndex
    }
}

/**
 * Same idea as [findTargetIndexInList] but comparing 2D center distance — a staggered grid's cells
 * don't form uniform rows, so "closest slot" has to account for horizontal position too.
 */
fun <T> findTargetIndexInGrid(
        gridState: LazyStaggeredGridState,
        dragState: DragReorderState<T>,
): Int? {
    val draggingKey = dragState.draggingKey ?: return null
    val visibleItems = gridState.layoutInfo.visibleItemsInfo
    return visibleItems.find { it.key == draggingKey }?.let { draggedInfo ->
        val desired = dragState.desiredPosition()
        val desiredCenter = Offset(
                x = desired.x + draggedInfo.size.width / 2f,
                y = desired.y + draggedInfo.size.height / 2f,
        )
        val targetIndex = visibleItems
            .minByOrNull { info ->
                val center = Offset(
                        x = info.offset.x + info.size.width / 2f,
                        y = info.offset.y + info.size.height / 2f,
                )
                (center - desiredCenter).getDistance()
            }
            ?.index
        // Same anchoring fix as findTargetIndexInList, for LazyStaggeredGridState.
        if (targetIndex != null &&
                (draggedInfo.index == gridState.firstVisibleItemIndex || targetIndex == gridState.firstVisibleItemIndex)
        ) {
            gridState.requestScrollToItem(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset)
        }
        targetIndex
    }
}

/**
 * The item's layout offset as if it weren't being dragged — what [dragReorderModifier] diffs
 * against [DragReorderState.desiredPosition] each frame to compute the translation.
 */
fun naturalOffsetInList(
        listState: LazyListState,
        key: Any,
): Offset? {
    val info = listState.layoutInfo.visibleItemsInfo.find { it.key == key } ?: return null
    return Offset(0f, info.offset.toFloat())
}

/** Grid counterpart of [naturalOffsetInList] — same purpose, but the offset has an X component too. */
fun naturalOffsetInGrid(
        gridState: LazyStaggeredGridState,
        key: Any,
): Offset? {
    val info = gridState.layoutInfo.visibleItemsInfo.find { it.key == key } ?: return null
    return Offset(info.offset.x.toFloat(), info.offset.y.toFloat())
}

/**
 * Scrolls while a drag is held near a viewport edge, so an item can travel further than one
 * screen. Callers supply the platform-specific bits (viewport bounds, item extent, how to scroll,
 * how to recompute the target slot); this loop only deals in floats.
 *
 * Runs until [draggingKey] no longer matches (a fresh drag gets its own call, from a
 * `LaunchedEffect` keyed on the dragging key). [withFrameNanos] paces this to one check per frame —
 * it's the loop's only suspension point when not near an edge (`scrollAmount == 0f` skips
 * [scrollBy]), so without it this would busy-spin. `proximity` ramps 0→1 across [edgeThreshold] so
 * scroll speed accelerates smoothly near the edge rather than snapping to [maxSpeedPerFrame].
 */
internal suspend fun <T> runDragAutoScroll(
        dragState: DragReorderState<T>,
        draggingKey: Any,
        density: Density,
        edgeThreshold: Dp,
        maxSpeedPerFrame: Dp,
        viewportStart: () -> Float,
        viewportEnd: () -> Float,
        draggedItemExtent: () -> Float?,
        scrollBy: suspend (Float) -> Float,
        recomputeTargetIndex: () -> Int?,
) {
    val edgeThresholdPx = with(density) { edgeThreshold.toPx() }
    val maxSpeedPx = with(density) { maxSpeedPerFrame.toPx() }
    while (currentCoroutineContext().isActive && dragState.draggingKey == draggingKey) {
        withFrameNanos { }
        val extent = draggedItemExtent() ?: continue
        val desiredTop = dragState.desiredPosition().y
        val desiredBottom = desiredTop + extent
        val start = viewportStart()
        val end = viewportEnd()
        val scrollAmount = when {
            desiredTop < start + edgeThresholdPx -> {
                val proximity = ((start + edgeThresholdPx - desiredTop) / edgeThresholdPx).coerceIn(0f, 1f)
                -maxSpeedPx * proximity
            }
            desiredBottom > end - edgeThresholdPx -> {
                val proximity = ((desiredBottom - (end - edgeThresholdPx)) / edgeThresholdPx).coerceIn(0f, 1f)
                maxSpeedPx * proximity
            }
            else -> 0f
        }
        if (scrollAmount != 0f) {
            val consumed = scrollBy(scrollAmount)
            dragState.adjustForScroll(consumed)
            val targetIndex = recomputeTargetIndex()
            if (targetIndex != null) {
                dragState.moveItem(dragState.currentDraggingIndex(), targetIndex)
            }
        }
    }
}

/**
 * Tap / long-press-to-select / long-press-then-drag-to-reorder gesture for one item, plus the
 * follow-the-finger transform while it's being dragged. Applied only when reordering should be
 * possible right now — the caller decides that (`.then(if (reorderEnabled) dragReorderModifier(...)
 * else Modifier)`), not an `enabled` flag here, so an inert item carries none of this overhead.
 *
 * Everything is recognized in one [pointerInput] rather than layering a `combinedClickable`
 * alongside it: two sibling pointer-input regions watching the same events race for which one
 * "wins" a press, with no guaranteed order. One coroutine handling every branch is what makes this
 * the sole owner of the pointer stream for the press.
 *
 * State machine per press:
 * 1. Released before the long-press threshold → [onClick].
 * 2. Long-press threshold reached, still down → [onLongPress] fires immediately (selection is the
 *    instant, primary reaction — it doesn't wait to see whether the finger moves next).
 * 3. [canEnterDrag] is read once, at step 2. If it was `true` and the pointer then passes
 *    [dragThreshold], the press escalates into a drag: [onExitSelection] undoes step 2, then
 *    [DragReorderState.startDrag] begins tracking. If `false`, the press is capped at step 2 for
 *    good — the eligibility check isn't repeated.
 * 4. Movement while dragging feeds [DragReorderState.accumulateDrag] and re-runs [findTargetIndex].
 * 5. Release while dragging reports the final index via [onReordered]; release after step 2 without
 *    crossing [dragThreshold] just leaves step 2's selection as the final state.
 *
 * Also does `combinedClickable`'s other job — feeding [interactionSource] so the caller's ripple
 * (`Modifier.indication`) reacts — since nothing does that for a hand-rolled [pointerInput]. One
 * [PressInteraction.Press] per press, paired with exactly one Release/Cancel at whichever exit
 * point the gesture takes; `press` is captured once and reused because indication matches
 * Release/Cancel to their Press by instance identity.
 */
@Composable
internal fun <T> dragReorderModifier(
        item: T,
        dragState: DragReorderState<T>,
        items: List<T>,
        shape: Shape,
        dragThreshold: Dp,
        interactionSource: MutableInteractionSource,
        findTargetIndex: () -> Int?,
        naturalOffset: () -> Offset?,
        canEnterDrag: () -> Boolean,
        onClick: () -> Unit,
        onLongPress: () -> Unit,
        onExitSelection: () -> Unit,
        onReordered: (item: T, targetIndex: Int) -> Unit,
): Modifier {
    val currentItems by rememberUpdatedState(items)
    val currentFindTargetIndex by rememberUpdatedState(findTargetIndex)
    val currentNaturalOffset by rememberUpdatedState(naturalOffset)
    val currentCanEnterDrag by rememberUpdatedState(canEnterDrag)
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongPress by rememberUpdatedState(onLongPress)
    val currentOnExitSelection by rememberUpdatedState(onExitSelection)
    val currentOnReordered by rememberUpdatedState(onReordered)
    val density = LocalDensity.current
    val dragThresholdPx = remember(density, dragThreshold) { with(density) { dragThreshold.toPx() } }
    val isDragging = dragState.isDragging(item)
    return Modifier
        // Paint order otherwise follows list index, so the instant a swap happens the *displaced*
        // neighbor (now sitting at a higher index than the dragged item) would draw on top of it.
        .zIndex(if (isDragging) 1f else 0f)
        // Always part of the chain (only its *content* branches on isDragging) — conditionally
        // inserting/removing a modifier ahead of pointerInput would tear down and recreate that
        // node mid-gesture, aborting an active drag the moment isDragging flips.
        .graphicsLayer {
            if (dragState.isDragging(item)) {
                val desired = dragState.desiredPosition()
                val natural = currentNaturalOffset() ?: desired
                translationX = desired.x - natural.x
                translationY = desired.y - natural.y
                // shadowElevation without an explicit shape draws a rectangular shadow, ignoring
                // whatever rounded .clip() the caller applies further down its own modifier chain.
                shadowElevation = 8f
                this.shape = shape
                clip = true
            } else {
                shadowElevation = 0f
            }
        }
        // Keyed on dragState.key(item), not just dragState, so this gesture restarts if the item
        // occupying this slot changes identity (e.g. after an external, non-drag reorder) rather
        // than continuing to track a press meant for a now-different item.
        .pointerInput(dragState, dragState.key(item)) {
            val longPressTimeoutMillis = viewConfiguration.longPressTimeoutMillis
            awaitEachGesture {
                val down = awaitFirstDown()
                val press = PressInteraction.Press(down.position)
                interactionSource.tryEmit(press)
                val eligibleForDrag = currentCanEnterDrag()
                // waitForUpOrCancellation() returns null both on a genuine up AND on cancellation
                // (e.g. the list claims the pointer for scrolling); withTimeoutOrNull adds timeout
                // as a third source of null. Wrapping the result as a Boolean collapses the first
                // two into `false`, so an outer `null` can only mean the timeout — otherwise a
                // cancellation would be misread as "long-press reached" and fall through to step 2
                // despite the pointer no longer being down.
                val releasedBeforeLongPress = withTimeoutOrNull(longPressTimeoutMillis) {
                    waitForUpOrCancellation() != null
                }
                when (releasedBeforeLongPress) {
                    true -> {
                        interactionSource.tryEmit(PressInteraction.Release(press))
                        currentOnClick()
                        return@awaitEachGesture
                    }
                    false -> {
                        // Not a tap (no up event) and not a long-press (threshold never elapsed) —
                        // whatever claimed the pointer owns this press now.
                        interactionSource.tryEmit(PressInteraction.Cancel(press))
                        return@awaitEachGesture
                    }
                    null -> Unit // genuine long-press timeout, pointer still down
                }
                currentOnLongPress()
                if (!eligibleForDrag) {
                    // Not eligible for drag — wait out the rest of the press without tracking movement.
                    val upChange = waitForUpOrCancellation()
                    interactionSource.tryEmit(
                            if (upChange != null) {
                                PressInteraction.Release(press)
                            } else {
                                PressInteraction.Cancel(press)
                            }
                    )
                    return@awaitEachGesture
                }
                var dragEntered = false
                var pendingDistance = 0f
                val completed = drag(down.id) { change ->
                    change.consume()
                    val delta = change.position - change.previousPosition
                    if (!dragEntered) {
                        pendingDistance += delta.getDistance()
                        if (pendingDistance >= dragThresholdPx) {
                            dragEntered = true
                            // Escalating into a drag — cancel the ripple rather than leaving it
                            // lingering under the lifted item.
                            interactionSource.tryEmit(PressInteraction.Cancel(press))
                            currentOnExitSelection()
                            val startOffset = currentNaturalOffset() ?: Offset.Zero
                            dragState.startDrag(item, currentItems, startOffset)
                        }
                    }
                    if (dragEntered) {
                        dragState.accumulateDrag(delta)
                        val targetIndex = currentFindTargetIndex()
                        if (targetIndex != null) {
                            dragState.moveItem(dragState.currentDraggingIndex(), targetIndex)
                        }
                    }
                }
                if (dragEntered) {
                    if (completed) {
                        val finalIndex = dragState.currentDraggingIndex()
                        dragState.endDrag()
                        currentOnReordered(item, finalIndex)
                    } else {
                        // Canceled rather than released — drop it, the caller's list is untouched.
                        dragState.endDrag()
                    }
                } else {
                    interactionSource.tryEmit(
                            if (completed) {
                                PressInteraction.Release(press)
                            } else {
                                PressInteraction.Cancel(press)
                            }
                    )
                }
            }
        }
}
