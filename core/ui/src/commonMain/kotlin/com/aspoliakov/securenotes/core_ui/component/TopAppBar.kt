package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
    androidx.compose.material3.TopAppBar(
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
            colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary,
            ),
    )
}
