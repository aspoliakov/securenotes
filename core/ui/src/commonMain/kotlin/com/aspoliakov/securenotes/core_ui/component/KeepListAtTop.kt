package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot

/**
 * Project SecureNotes
 */

/**
 * Lazy layouts keep the first visible item in place by its key, so when items are inserted above it
 * or reordered, the list follows that item down. Instead, the list stays at the top if it was there.
 * The scroll is requested during composition, so it applies in the same measure pass as the new [items].
 */
@Composable
fun KeepListAtTop(
        state: LazyListState,
        items: List<*>,
) {
    KeepListAtTop(items) {
        if (state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0) {
            state.requestScrollToItem(0)
        }
    }
}

@Composable
fun KeepListAtTop(
        state: LazyStaggeredGridState,
        items: List<*>,
) {
    KeepListAtTop(items) {
        if (state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0) {
            state.requestScrollToItem(0)
        }
    }
}

@Composable
private fun KeepListAtTop(
        items: List<*>,
        keepAtTop: () -> Unit,
) {
    val applied = remember { AppliedItems(items) }
    if (items !== applied.items) {
        Snapshot.withoutReadObservation(keepAtTop)
        applied.items = items
    }
}

private class AppliedItems(var items: List<*>)
