package com.aspoliakov.securenotes.feature_profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.ShimmerEffect
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.feature_profile_about
import com.aspoliakov.securenotes.core_ui.resources.feature_profile_avatar_description
import com.aspoliakov.securenotes.core_ui.resources.feature_profile_logout
import com.aspoliakov.securenotes.core_ui.resources.profile
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Project SecureNotes
 */

@Composable
fun ProfileScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    ProfileScreen(
            modifier = modifier,
            state = viewModel.currentState,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun ProfileScreen(
        modifier: Modifier = Modifier,
        state: ProfileState = ProfileState(),
        intentHandler: (ProfileIntent) -> Unit = {},
) {
    Column(
            modifier = modifier
                    .padding(top = 16.dp)
                    .fillMaxSize(),
    ) {
        ProfileDataView(
                modifier = modifier,
                profileDataState = state.profileDataState,
        )
        AboutButton(
                onClick = { intentHandler.invoke(ProfileIntent.OnAboutClick) }
        )
        LogoutButton(
                onClick = { intentHandler.invoke(ProfileIntent.OnLogoutClick) }
        )
    }
}

@Composable
internal fun ProfileDataView(
        modifier: Modifier = Modifier,
        profileDataState: ProfileDataState = ProfileDataState.Idle,
) {
    Card(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(CardDefaults.shape),
            border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
            ),
            colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
        ) {
            when (profileDataState) {
                is ProfileDataState.Idle -> {
                    ShimmerEffect(
                            modifier = modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                    )
                    ShimmerEffect(
                            modifier = modifier
                                    .padding(start = 12.dp)
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(CircleShape),
                    )
                }
                is ProfileDataState.Loaded -> {
                    Image(
                            modifier = modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(40.dp)),
                            painter = if (profileDataState.avatar != null) {
                                painterResource(Res.drawable.profile)
                            } else {
                                rememberVectorPainter(Icons.Avatar)
                            },
                            contentDescription = stringResource(Res.string.feature_profile_avatar_description),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    )
                    Text(
                            modifier = Modifier
                                    .padding(
                                            horizontal = 12.dp,
                                            vertical = 4.dp,
                                    ),
                            text = profileDataState.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Normal,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
internal fun AboutButton(
        onClick: () -> Unit = {},
) {
    ProfileItemButton(
            imageVector = Icons.About,
            text = Res.string.feature_profile_about,
            onClick = onClick,
    )
}

@Composable
internal fun LogoutButton(
        onClick: () -> Unit = {},
) {
    ProfileItemButton(
            imageVector = Icons.Logout,
            text = Res.string.feature_profile_logout,
            onClick = onClick,
    )
}

@Composable
internal fun ProfileItemButton(
        imageVector: ImageVector,
        text: StringResource,
        contentDescription: StringResource = text,
        onClick: () -> Unit,
) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
                modifier = Modifier
                        .size(24.dp),
                imageVector = imageVector,
                contentDescription = stringResource(contentDescription),
                tint = MaterialTheme.colorScheme.secondary,
        )
        Text(
                modifier = Modifier
                        .padding(horizontal = 12.dp),
                text = stringResource(text),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                ),
        )
    }
}
