package com.aspoliakov.securenotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.app_name
import org.jetbrains.compose.resources.stringResource

/**
 * Project Express. Created by Unlimited Production team.
 */

@Composable
internal fun SplashScreen(
        modifier: Modifier = Modifier,
) {
    Scaffold(
            modifier = modifier
    ) { paddings ->
        Column(
                modifier = modifier
                        .padding(paddings)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
        ) {
            Text(
                    text = stringResource(Res.string.app_name),
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
