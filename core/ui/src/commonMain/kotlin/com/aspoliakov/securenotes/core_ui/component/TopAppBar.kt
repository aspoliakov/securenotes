package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.aspoliakov.securenotes.core_ui.Icons

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
        title: String = "",
        onBackClick: () -> Unit = {},
) {
    TopAppBar(
            navigationIcon = {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                            imageVector = Icons.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            title = {
                Text(
                        text = title,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                )
            },
    )
}
