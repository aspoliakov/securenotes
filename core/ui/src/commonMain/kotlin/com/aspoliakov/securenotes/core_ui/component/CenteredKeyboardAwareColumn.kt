package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Project SecureNotes
 */

/**
 * A vertically centered, scrollable form container that stays visually stable while the
 * keyboard opens/closes. [Arrangement.Center] recalculates every frame as the available
 * height shrinks during the IME animation, which reads as the content jumping; fixing the
 * column's minimum height to the pre-keyboard viewport height keeps centering stable while
 * [Modifier.verticalScroll] still lets content scroll above the keyboard when needed.
 */
@Composable
fun CenteredKeyboardAwareColumn(
        modifier: Modifier = Modifier,
        contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
        content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(
            modifier = modifier.fillMaxSize(),
    ) {
        Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = horizontalAlignment,
                content = content,
        )
    }
}
