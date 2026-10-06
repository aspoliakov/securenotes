package com.aspoliakov.securenotes.feature_note.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppPreview
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.NoteShape
import com.aspoliakov.securenotes.core_ui.component.noteContainerColor
import com.aspoliakov.securenotes.core_ui.resources.*
import com.aspoliakov.securenotes.domain_notes.model.NoteColor
import com.aspoliakov.securenotes.feature_note.presentation.styled_text.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

/**
 * Project SecureNotes
 */

@Composable
fun NoteScreenRoute(
        modifier: Modifier = Modifier,
        onNavigationBack: () -> Unit,
        noteId: String?,
        folderId: String? = null,
) {
    val viewModel = koinMviViewModel<NoteViewModel>(parameters = { parametersOf(noteId, folderId) })
    val state by viewModel.state.collectAsState()
    NoteScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            onNavigationBack = onNavigationBack,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun NoteScreen(
        modifier: Modifier = Modifier,
        state: NoteState = NoteState(),
        effects: Flow<Effect> = emptyFlow(),
        onNavigationBack: () -> Unit,
        intentHandler: (NoteIntent) -> Unit = {},
) {
    CollectEffects<NoteEffect>(effects) { effect ->
        when (effect) {
            is NoteEffect.Close -> onNavigationBack()
        }
    }
    var showColorPicker by remember { mutableStateOf(false) }
    val bodyState = rememberStyledTextState(state.body)
    LaunchedEffect(bodyState) {
        snapshotFlow { bodyState.text }
            .drop(1)
            .collect { intentHandler(NoteIntent.OnBodyChanged(it)) }
    }
    Scaffold(
            topBar = {
                NoteToolbar(
                        showDelete = state.noteId != null,
                        onNavigationBack = onNavigationBack,
                        onDeleteClick = { intentHandler.invoke(NoteIntent.OnDeleteClick) }
                )
            }
    ) { padding ->
        Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding(),
        ) {
            BoxWithConstraints(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(NoteShape)
                        .background(noteContainerColor(state.color.argb)),
            ) {
                val viewportHeight = maxHeight
                val density = LocalDensity.current
                var titleHeight by remember { mutableStateOf(0.dp) }
                Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 4.dp),
                ) {
                    NoteTitle(
                            modifier = Modifier.onSizeChanged { titleHeight = with(density) { it.height.toDp() } },
                            value = state.title,
                            onValueChange = { intentHandler(NoteIntent.OnTitleChanged(it)) },
                    )
                    NoteBody(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = (viewportHeight - titleHeight).coerceAtLeast(0.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            state = bodyState,
                    )
                }
            }
            NoteBottomBar(
                    stylesEnabled = bodyState.isFocused,
                    onStyleClick = bodyState::applyStyle,
                    onColorPickerClick = { showColorPicker = true },
            )
        }
    }
    if (showColorPicker) {
        NoteColorPickerSheet(
                selectedColor = state.color,
                onColorSelected = { intentHandler(NoteIntent.OnColorSelected(it)) },
                onDismiss = { showColorPicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteToolbar(
        showDelete: Boolean,
        onNavigationBack: () -> Unit,
        onDeleteClick: () -> Unit,
) {
    TopAppBar(
            navigationIcon = {
                IconButton(
                        onClick = { onNavigationBack() },
                ) {
                    Icon(
                            imageVector = Icons.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            title = {
                Text(
                        text = "",
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                )
            },
            actions = {
                if (showDelete) {
                    IconButton(
                            onClick = onDeleteClick,
                    ) {
                        Icon(
                                imageVector = Icons.Delete,
                                contentDescription = stringResource(Res.string.common_delete),
                                tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
    )
}

@Composable
internal fun NoteTitle(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
) {
    val style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Normal,
    )
    TextField(
            modifier = modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            textStyle = style,
            placeholder = {
                Text(
                        style = style.copy(
                                color = style.color.copy(alpha = 0.4F),
                        ),
                        text = stringResource(Res.string.feature_note_text_field_title_hint)
                )
            },
            colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
            ),
    )
}

@Composable
internal fun NoteBody(
        modifier: Modifier = Modifier,
        state: StyledTextState,
) {
    StyledTextEditor(
            modifier = modifier,
            state = state,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
            ),
            placeholder = stringResource(Res.string.feature_note_text_field_body_hint),
    )
}

@Composable
internal fun NoteBottomBar(
        stylesEnabled: Boolean,
        onStyleClick: (TextStyleAction) -> Unit,
        onColorPickerClick: () -> Unit,
) {
    var stylesExpanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(stylesEnabled) {
        if (!stylesEnabled) stylesExpanded = false
    }
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        if (stylesExpanded) {
            IconButton(
                    modifier = Modifier.focusProperties { canFocus = false },
                    onClick = { stylesExpanded = false },
            ) {
                Icon(
                        imageVector = Icons.Close,
                        contentDescription = stringResource(Res.string.feature_note_text_styles_close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            VerticalDivider(modifier = Modifier.height(24.dp))
            TextStylesToolbar(
                    modifier = Modifier.weight(1F),
                    onStyleClick = onStyleClick,
            )
        } else {
            IconButton(
                    modifier = Modifier.focusProperties { canFocus = false },
                    enabled = stylesEnabled,
                    onClick = { stylesExpanded = true },
            ) {
                Icon(
                        imageVector = Icons.TextStyles,
                        contentDescription = stringResource(Res.string.feature_note_text_styles),
                )
            }
            NoteColorPickerButton(
                    onClick = onColorPickerClick,
            )
        }
    }
}

@Composable
internal fun NoteColorPickerButton(
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
) {
    IconButton(
            modifier = modifier,
            onClick = onClick,
    ) {
        Icon(
                imageVector = Icons.ColorPalette,
                contentDescription = stringResource(Res.string.feature_note_color_picker),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteColorPickerSheet(
        selectedColor: NoteColor,
        onColorSelected: (NoteColor) -> Unit,
        onDismiss: () -> Unit,
) {
    ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            NoteColor.entries.forEach { color ->
                NoteColorItem(
                        color = color,
                        selected = color == selectedColor,
                        onClick = { onColorSelected(color) },
                )
            }
        }
    }
}

@Composable
private fun NoteColorItem(
        color: NoteColor,
        selected: Boolean,
        onClick: () -> Unit,
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
    ) {
        Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(color.argb?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                            width = if (selected) 3.dp else 1.dp,
                            color = borderColor,
                            shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
        ) {
            if (color == NoteColor.DEFAULT) {
                Icon(
                        imageVector = Icons.NoColor,
                        contentDescription = stringResource(Res.string.feature_note_color_none),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Composable
private fun NoteScreenPreview() {
    AppPreview {
        NoteScreen(
                state = NoteState(
                        noteId = "note_id",
                        newNote = false,
                        title = "Title",
                        body = "Body",
                        color = NoteColor.BLUE,
                ),
                onNavigationBack = {},
        )
    }
}
