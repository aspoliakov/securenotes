package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.toSize
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
fun ButtonWithLoader(
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
        isLoading: Boolean,
        stringResource: StringResource,
) {
    var buttonSize by key(stringResource) { remember { mutableStateOf(DpSize.Zero) } }
    val density = LocalDensity.current
    OutlinedButton(
            modifier = modifier
                    .then(
                            if (buttonSize != DpSize.Zero) {
                                Modifier.size(buttonSize)
                            } else {
                                Modifier
                            }
                    )
                    .onSizeChanged { newSize ->
                        if (buttonSize == DpSize.Zero) {
                            buttonSize = with(density) {
                                newSize
                                        .toSize()
                                        .toDpSize()
                            }
                        }
                    },
            enabled = !isLoading,
            onClick = onClick,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                    modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
            )
        } else {
            Text(
                    text = stringResource(stringResource),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
