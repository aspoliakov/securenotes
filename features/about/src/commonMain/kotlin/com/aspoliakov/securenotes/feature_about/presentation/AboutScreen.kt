package com.aspoliakov.securenotes.feature_about.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.LocalCustomColorSchemeProvider
import com.aspoliakov.securenotes.core_ui.component.TopAppBar
import com.aspoliakov.securenotes.core_ui.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.material.icons.Icons as MaterialIcons

/**
 * Project SecureAbouts
 */

@Composable
fun AboutScreenRoute(
        modifier: Modifier = Modifier,
        onNavigationBack: () -> Unit = {},
) {
    val viewModel = koinMviViewModel<AboutViewModel>()
    val state by viewModel.state.collectAsState()
    AboutScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            onNavigationBack = onNavigationBack,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun AboutScreen(
        modifier: Modifier = Modifier,
        state: AboutState = AboutState(),
        effects: Flow<Effect> = emptyFlow(),
        onNavigationBack: () -> Unit = {},
        intentHandler: (AboutIntent) -> Unit = {},
) {
    CollectEffects<AboutEffect>(effects) { effect ->
        when (effect) {
            is AboutEffect.ShowSnackbar -> {}
        }
    }
    Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                        title = stringResource(Res.string.feature_profile_about),
                        onBackClick = onNavigationBack,
                )
            },
    ) { paddings ->
        Column(
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Image(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(LocalCustomColorSchemeProvider.current.logoBackground)
                        .padding(22.dp),
                    painter = painterResource(Res.drawable.app_logo_auth),
                    contentDescription = stringResource(Res.string.app_name),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.feature_about_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))
            FeatureHighlights()
            Spacer(modifier = Modifier.weight(1f))
            VersionPill(
                    appVersion = state.appVersion,
                    onClick = { intentHandler(AboutIntent) },
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
internal fun FeatureHighlights(
        modifier: Modifier = Modifier,
) {
    Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        FeatureRow(
                imageVector = Icons.Security,
                title = stringResource(Res.string.feature_about_highlight_encryption_title),
                subtitle = stringResource(Res.string.feature_about_highlight_encryption_subtitle),
                iconTint = MaterialTheme.colorScheme.primary,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        )
        HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
        )
        FeatureRow(
                imageVector = MaterialIcons.Filled.Devices,
                title = stringResource(Res.string.feature_about_highlight_cross_platform_title),
                subtitle = stringResource(Res.string.feature_about_highlight_cross_platform_subtitle),
                iconTint = MaterialTheme.colorScheme.tertiary,
                iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
        )
    }
}

@Composable
internal fun FeatureRow(
        imageVector: ImageVector,
        title: String,
        subtitle: String,
        iconTint: Color,
        iconContainerColor: Color,
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center,
        ) {
            Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = iconTint,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun VersionPill(
        modifier: Modifier = Modifier,
        appVersion: String,
        onClick: () -> Unit = {},
) {
    Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
                text = stringResource(Res.string.feature_about_app_version, appVersion),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    AppTheme {
        AboutScreen(
                state = AboutState(appVersion = "1.0.0"),
        )
    }
}
