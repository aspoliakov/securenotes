package com.aspoliakov.securenotes.feature_profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.ShimmerEffect
import com.aspoliakov.securenotes.core_ui.component.Spacer16dp
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.feature_profile_about
import com.aspoliakov.securenotes.core_ui.resources.feature_profile_avatar_description
import com.aspoliakov.securenotes.core_ui.resources.feature_profile_logout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
fun ProfileScreenRoute(
        modifier: Modifier = Modifier,
        onNavigateToAbout: () -> Unit,
) {
    val viewModel = koinMviViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsState()
    ProfileScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            onNavigateToAbout = onNavigateToAbout,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun ProfileScreen(
        modifier: Modifier = Modifier,
        state: ProfileState = ProfileState(),
        effects: Flow<Effect> = emptyFlow(),
        onNavigateToAbout: () -> Unit,
        intentHandler: (ProfileIntent) -> Unit = {},
) {
    CollectEffects<ProfileEffect>(effects) { effect ->
        when (effect) {
            else -> {}
        }
    }
    Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        ProfileHeader(
                modifier = Modifier.fillMaxWidth(),
                profileDataState = state.profileDataState,
        )
        Spacer(modifier = Modifier.height(48.dp))
        ProfileActions(
                onAboutClick = { onNavigateToAbout() },
                onLogoutClick = { intentHandler(ProfileIntent.OnLogoutClick) },
        )
    }
}

@Composable
internal fun ProfileHeader(
        modifier: Modifier = Modifier,
        profileDataState: ProfileDataState = ProfileDataState.Idle,
) {
    Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (profileDataState) {
            is ProfileDataState.Idle -> {
                ShimmerEffect(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                )
                Spacer16dp()
                ShimmerEffect(
                        modifier = Modifier
                            .width(140.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp)),
                )
            }
            is ProfileDataState.Loaded -> {
                ProfileAvatar(
                        name = profileDataState.name,
                        avatar = profileDataState.avatar,
                )
                Spacer16dp()
                Text(
                        text = profileDataState.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
internal fun ProfileAvatar(
        modifier: Modifier = Modifier,
        name: String,
        avatar: String? = null,
) {
    if (avatar != null) {
        AsyncImage(
                modifier = modifier
                    .size(88.dp)
                    .clip(CircleShape),
                model = avatar,
                imageLoader = rememberAvatarImageLoader(),
                contentDescription = stringResource(Res.string.feature_profile_avatar_description),
                contentScale = ContentScale.Crop,
        )
    } else {
        val initials = remember(name) {
            name.trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .joinToString("")
        }
        Box(
                modifier = modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
        ) {
            Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun rememberAvatarImageLoader(): ImageLoader {
    val context = LocalPlatformContext.current
    return remember(context) {
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }
}

@Composable
internal fun ProfileActions(
        modifier: Modifier = Modifier,
        onAboutClick: () -> Unit,
        onLogoutClick: () -> Unit,
) {
    Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        ProfileActionRow(
                imageVector = Icons.About,
                text = Res.string.feature_profile_about,
                iconTint = MaterialTheme.colorScheme.primary,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = onAboutClick,
        )
        HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
        )
        ProfileActionRow(
                imageVector = Icons.Logout,
                text = Res.string.feature_profile_logout,
                iconTint = MaterialTheme.colorScheme.error,
                iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                onClick = onLogoutClick,
        )
    }
}

@Composable
internal fun ProfileActionRow(
        imageVector: ImageVector,
        text: StringResource,
        iconTint: Color,
        iconContainerColor: Color,
        onClick: () -> Unit,
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center,
        ) {
            Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = imageVector,
                    contentDescription = stringResource(text),
                    tint = iconTint,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
                text = stringResource(text),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    AppTheme {
        ProfileScreen(
                state = ProfileState(
                        profileDataState = ProfileDataState.Loaded(
                                name = "Anton Poliakov",
                                avatar = null,
                        ),
                ),
                onNavigateToAbout = {},
        )
    }
}

@Preview
@Composable
private fun ProfileScreenLoadingPreview() {
    AppTheme {
        ProfileScreen(
                state = ProfileState(),
                onNavigateToAbout = {},
        )
    }
}
