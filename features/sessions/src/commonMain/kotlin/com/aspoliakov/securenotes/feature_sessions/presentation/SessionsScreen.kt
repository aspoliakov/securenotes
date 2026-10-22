package com.aspoliakov.securenotes.feature_sessions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppPreview
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.TopAppBar
import com.aspoliakov.securenotes.core_ui.resources.*
import com.aspoliakov.securenotes.domain_user_state.model.ActiveSession
import com.aspoliakov.securenotes.domain_user_state.model.SessionPlatform
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

private val sessionDateFormat = LocalDateTime.Format {
    day(padding = Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year()
    chars(", ")
    hour()
    char(':')
    minute()
}

@Composable
fun SessionsScreenRoute(
        modifier: Modifier = Modifier,
        onNavigationBack: () -> Unit = {},
) {
    val viewModel = koinMviViewModel<SessionsViewModel>()
    val state by viewModel.state.collectAsState()
    SessionsScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            onNavigationBack = onNavigationBack,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun SessionsScreen(
        modifier: Modifier = Modifier,
        state: SessionsState = SessionsState(),
        effects: Flow<Effect> = emptyFlow(),
        onNavigationBack: () -> Unit = {},
        intentHandler: (SessionsIntent) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    CollectEffects<SessionsEffect>(effects) { effect ->
        when (effect) {
            is SessionsEffect.ShowTerminateError -> coroutineScope.launch {
                snackbarHostState.showSnackbar(getString(Res.string.feature_sessions_terminate_error))
            }
        }
    }
    val contentState = state.contentState
    val hasOtherSessions = contentState is SessionsContentState.Loaded && contentState.otherSessions.isNotEmpty()
    Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                        title = stringResource(Res.string.feature_profile_sessions),
                        onBackClick = onNavigationBack,
                        actions = {
                            if (hasOtherSessions) {
                                IconButton(
                                        enabled = !state.isTerminating,
                                        onClick = { intentHandler(SessionsIntent.OnTerminateOtherSessionsClick) },
                                ) {
                                    Icon(
                                            imageVector = Icons.TerminateSessions,
                                            contentDescription = stringResource(
                                                    Res.string.feature_sessions_terminate_others,
                                            ),
                                    )
                                }
                            }
                        },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddings ->
        Box(
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize(),
        ) {
            when (contentState) {
                is SessionsContentState.Loading -> SessionsLoadingView()
                is SessionsContentState.Error -> SessionsErrorView(
                        onRetryClick = { intentHandler(SessionsIntent.OnRetryClick) },
                )
                is SessionsContentState.Loaded -> SessionsList(
                        contentState = contentState,
                        isTerminating = state.isTerminating,
                        onTerminateClick = { intentHandler(SessionsIntent.OnTerminateSessionClick(it)) },
                )
            }
            if (state.isTerminating) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
    state.confirmation?.let { confirmation ->
        TerminateConfirmationDialog(
                confirmation = confirmation,
                onConfirm = { intentHandler(SessionsIntent.OnConfirmationConfirmed) },
                onDismiss = { intentHandler(SessionsIntent.OnConfirmationDismissed) },
        )
    }
}

@Composable
internal fun SessionsLoadingView() {
    Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
internal fun SessionsErrorView(
        onRetryClick: () -> Unit,
) {
    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        Text(
                text = stringResource(Res.string.feature_sessions_load_error),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = onRetryClick) {
            Text(text = stringResource(Res.string.common_retry))
        }
    }
}

@Composable
internal fun SessionsList(
        contentState: SessionsContentState.Loaded,
        isTerminating: Boolean,
        onTerminateClick: (ActiveSession) -> Unit,
) {
    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        val currentSession = contentState.currentSession
        if (currentSession != null) {
            sectionHeader(Res.string.feature_sessions_current)
            item(key = currentSession.id) {
                SessionsGroup {
                    SessionRow(session = currentSession)
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
        sectionHeader(Res.string.feature_sessions_others)
        if (contentState.otherSessions.isEmpty()) {
            item {
                Text(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        text = stringResource(Res.string.feature_sessions_no_others),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            item {
                SessionsGroup {
                    contentState.otherSessions.forEachIndexed { index, session ->
                        if (index > 0) {
                            HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                        SessionRow(
                                session = session,
                                isTerminateEnabled = !isTerminating,
                                onTerminateClick = { onTerminateClick(session) },
                        )
                    }
                }
            }
        }
    }
}

private fun LazyListScope.sectionHeader(title: StringResource) {
    item {
        Text(
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                text = stringResource(title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SessionsGroup(
        content: @Composable ColumnScope.() -> Unit,
) {
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
            content = content,
    )
}

@Composable
internal fun SessionRow(
        session: ActiveSession,
        isTerminateEnabled: Boolean = true,
        onTerminateClick: (() -> Unit)? = null,
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
        ) {
            Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = session.platform.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = session.deviceName ?: stringResource(Res.string.feature_sessions_unknown_device),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
            )
            session.platformTitle()?.let { platformTitle ->
                Text(
                        text = platformTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            session.appVersion?.let { appVersion ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = stringResource(Res.string.feature_sessions_app_version, appVersion),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = stringResource(Res.string.feature_sessions_signed_in, session.createdAt.formatted()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (session.isCurrent) {
                Text(
                        text = stringResource(Res.string.feature_sessions_active_now),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(
                        text = stringResource(
                                Res.string.feature_sessions_last_active,
                                session.lastActiveAt.formatted(),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (onTerminateClick != null) {
            IconButton(
                    enabled = isTerminateEnabled,
                    onClick = onTerminateClick,
            ) {
                Icon(
                        imageVector = Icons.Logout,
                        contentDescription = stringResource(Res.string.feature_sessions_terminate),
                        tint = if (isTerminateEnabled) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}

@Composable
internal fun TerminateConfirmationDialog(
        confirmation: SessionsConfirmation,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
) {
    val title: String
    val message: String
    when (confirmation) {
        is SessionsConfirmation.TerminateSession -> {
            title = stringResource(Res.string.feature_sessions_terminate_title)
            message = stringResource(
                    Res.string.feature_sessions_terminate_message,
                    confirmation.session.deviceName ?: stringResource(Res.string.feature_sessions_unknown_device),
            )
        }
        is SessionsConfirmation.TerminateOtherSessions -> {
            title = stringResource(Res.string.feature_sessions_terminate_others_title)
            message = stringResource(Res.string.feature_sessions_terminate_others_message)
        }
    }
    AlertDialog(
            icon = { Icon(imageVector = Icons.TerminateSessions, contentDescription = null) },
            title = { Text(text = title) },
            text = { Text(text = message) },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(
                            text = stringResource(Res.string.feature_sessions_terminate_confirm),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(Res.string.common_cancel))
                }
            },
    )
}

@Composable
private fun ActiveSession.platformTitle(): String? {
    val platformName = when (platform) {
        SessionPlatform.ANDROID -> stringResource(Res.string.feature_sessions_platform_android)
        SessionPlatform.IOS -> stringResource(Res.string.feature_sessions_platform_ios)
        SessionPlatform.UNKNOWN -> return null
    }
    return listOfNotNull(platformName, osVersion).joinToString(" ")
}

private fun SessionPlatform.icon(): ImageVector {
    return when (this) {
        SessionPlatform.ANDROID -> Icons.SessionAndroid
        SessionPlatform.IOS -> Icons.SessionIos
        SessionPlatform.UNKNOWN -> Icons.Sessions
    }
}

private fun Instant.formatted(): String {
    return toLocalDateTime(TimeZone.currentSystemDefault()).format(sessionDateFormat)
}

@Preview
@Composable
private fun SessionsScreenPreview() {
    val now = Clock.System.now()
    AppPreview {
        SessionsScreen(
                state = SessionsState(
                        contentState = SessionsContentState.Loaded(
                                currentSession = ActiveSession(
                                        id = "1",
                                        deviceName = "Google Pixel 8",
                                        platform = SessionPlatform.ANDROID,
                                        osVersion = "15",
                                        appVersion = "0.1.1",
                                        createdAt = now - 30.days,
                                        lastActiveAt = now,
                                        isCurrent = true,
                                ),
                                otherSessions = listOf(
                                        ActiveSession(
                                                id = "2",
                                                deviceName = "iPhone",
                                                platform = SessionPlatform.IOS,
                                                osVersion = "26.0",
                                                appVersion = "0.1.0",
                                                createdAt = now - 3.days,
                                                lastActiveAt = now - 5.hours,
                                                isCurrent = false,
                                        ),
                                        ActiveSession(
                                                id = "3",
                                                deviceName = null,
                                                platform = SessionPlatform.UNKNOWN,
                                                osVersion = null,
                                                appVersion = null,
                                                createdAt = now - 60.days,
                                                lastActiveAt = now - 20.days,
                                                isCurrent = false,
                                        ),
                                ),
                        ),
                ),
        )
    }
}

@Preview
@Composable
private fun SessionsScreenLoadingPreview() {
    AppPreview {
        SessionsScreen(state = SessionsState())
    }
}

@Preview
@Composable
private fun SessionsScreenErrorPreview() {
    AppPreview {
        SessionsScreen(state = SessionsState(contentState = SessionsContentState.Error))
    }
}
