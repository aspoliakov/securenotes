package com.aspoliakov.securenotes.feature_note.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.navigation.passInputThroughWhileExiting
import com.aspoliakov.securenotes.core_presentation.navigation.screenChromeTransition
import com.aspoliakov.securenotes.core_presentation.navigation.sharedNoteBounds
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppPreview
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.FloatingButtonGroup
import com.aspoliakov.securenotes.core_ui.component.NoteShape
import com.aspoliakov.securenotes.core_ui.component.noteContainerColor
import com.aspoliakov.securenotes.core_ui.resources.*
import com.aspoliakov.securenotes.domain_notes.model.NoteColor
import com.aspoliakov.securenotes.feature_note.presentation.editor.NoteEditorState
import com.aspoliakov.securenotes.feature_note.presentation.editor.rememberNoteEditorState
import com.aspoliakov.securenotes.feature_note.presentation.styled_text.StyledTextEditor
import com.aspoliakov.securenotes.feature_note.presentation.styled_text.StyledTextState
import com.aspoliakov.securenotes.feature_note.presentation.styled_text.TextStylesToolbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

/**
 * Project SecureNotes
 */

private val ScreenHorizontalPadding = 12.dp
private val BarVerticalPadding = 8.dp

private const val BAR_ENTER_DURATION_MILLIS = 250
private const val BAR_EXIT_DURATION_MILLIS = 150
private const val BAR_CONTENT_FADE_IN_MILLIS = 200
private const val BAR_CONTENT_FADE_OUT_MILLIS = 120
private val FloatingButtonsSpacing = 12.dp

private val TopBarEnterTransition = fadeIn(tween(BAR_ENTER_DURATION_MILLIS)) +
        slideInVertically(tween(BAR_ENTER_DURATION_MILLIS)) { -it / 2 }
private val TopBarExitTransition = fadeOut(tween(BAR_EXIT_DURATION_MILLIS)) +
        slideOutVertically(tween(BAR_EXIT_DURATION_MILLIS)) { -it / 2 }
private val BottomBarEnterTransition = fadeIn(tween(BAR_ENTER_DURATION_MILLIS)) +
        slideInVertically(tween(BAR_ENTER_DURATION_MILLIS)) { it / 2 }
private val BottomBarExitTransition = fadeOut(tween(BAR_EXIT_DURATION_MILLIS)) +
        slideOutVertically(tween(BAR_EXIT_DURATION_MILLIS)) { it / 2 }

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
    val editorState = rememberNoteEditorState(initialTitle = state.title, initialBody = state.body)
    LaunchedEffect(editorState) {
        snapshotFlow { editorState.title.text }
            .drop(1)
            .collect { intentHandler(NoteIntent.OnTitleChanged(it)) }
    }
    LaunchedEffect(editorState) {
        snapshotFlow { editorState.body.text }
            .drop(1)
            .collect { intentHandler(NoteIntent.OnBodyChanged(it)) }
    }
    Column(
            modifier = modifier
                .passInputThroughWhileExiting()
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = ScreenHorizontalPadding),
    ) {
        NoteTopBar(
                modifier = Modifier
                    .padding(vertical = BarVerticalPadding)
                    .screenChromeTransition(enter = TopBarEnterTransition, exit = TopBarExitTransition),
                showDelete = state.noteId != null,
                onNavigationBack = onNavigationBack,
                onDeleteClick = { intentHandler.invoke(NoteIntent.OnDeleteClick) },
        )
        NoteCard(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                noteId = state.noteId,
                color = state.color,
        ) { viewportHeight ->
            val density = LocalDensity.current
            var titleHeight by remember { mutableStateOf(0.dp) }
            NoteTitle(
                    modifier = Modifier.onSizeChanged { titleHeight = with(density) { it.height.toDp() } },
                    value = editorState.title,
                    onValueChange = { editorState.title = it },
            )
            NoteBody(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = (viewportHeight - titleHeight).coerceAtLeast(0.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    state = editorState.body,
            )
        }
        NoteBottomBar(
                modifier = Modifier
                    .padding(vertical = BarVerticalPadding)
                    .screenChromeTransition(enter = BottomBarEnterTransition, exit = BottomBarExitTransition),
                editorState = editorState,
                onColorPickerClick = { showColorPicker = true },
        )
    }
    if (showColorPicker) {
        NoteColorPickerSheet(
                selectedColor = state.color,
                onColorSelected = { intentHandler(NoteIntent.OnColorSelected(it)) },
                onDismiss = { showColorPicker = false },
        )
    }
}

@Composable
private fun NoteCard(
        modifier: Modifier = Modifier,
        noteId: String?,
        color: NoteColor,
        content: @Composable ColumnScope.(viewportHeight: Dp) -> Unit,
) {
    BoxWithConstraints(
            modifier = modifier
                .sharedNoteBounds(
                        noteId = noteId,
                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.TopCenter,
                        ),
                )
                .clip(NoteShape)
                .background(noteContainerColor(color.argb)),
    ) {
        val viewportHeight = maxHeight
        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp),
        ) {
            content(viewportHeight)
        }
    }
}

@Composable
internal fun NoteTopBar(
        modifier: Modifier = Modifier,
        showDelete: Boolean,
        onNavigationBack: () -> Unit,
        onDeleteClick: () -> Unit,
) {
    Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingButtonGroup {
            IconButton(
                    onClick = onNavigationBack,
            ) {
                Icon(
                        imageVector = Icons.ArrowBack,
                        contentDescription = stringResource(Res.string.common_back),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1F))
        AnimatedVisibility(
                visible = showDelete,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
        ) {
            FloatingButtonGroup {
                IconButton(
                        onClick = onDeleteClick,
                ) {
                    Icon(
                            imageVector = Icons.Delete,
                            contentDescription = stringResource(Res.string.common_delete),
                    )
                }
            }
        }
    }
}

@Composable
internal fun NoteTitle(
        modifier: Modifier = Modifier,
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
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
        modifier: Modifier = Modifier,
        editorState: NoteEditorState,
        onColorPickerClick: () -> Unit,
) {
    val stylesEnabled = editorState.body.isFocused
    var stylesExpanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(stylesEnabled) {
        if (!stylesEnabled) stylesExpanded = false
    }
    Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FloatingButtonsSpacing),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
                modifier = Modifier.weight(1F),
                targetState = stylesExpanded,
                transitionSpec = {
                    fadeIn(tween(BAR_CONTENT_FADE_IN_MILLIS)) togetherWith fadeOut(tween(BAR_CONTENT_FADE_OUT_MILLIS))
                },
                contentAlignment = Alignment.CenterStart,
        ) { expanded ->
            Row(
                    horizontalArrangement = Arrangement.spacedBy(FloatingButtonsSpacing),
                    verticalAlignment = Alignment.CenterVertically,
            ) {
                if (expanded) {
                    FloatingButtonGroup {
                        IconButton(
                                modifier = Modifier.focusProperties { canFocus = false },
                                onClick = { stylesExpanded = false },
                        ) {
                            Icon(
                                    imageVector = Icons.Close,
                                    contentDescription = stringResource(Res.string.feature_note_text_styles_close),
                            )
                        }
                    }
                    FloatingButtonGroup(
                            modifier = Modifier.weight(1F, fill = false),
                    ) {
                        TextStylesToolbar(
                                onStyleClick = editorState::applyStyle,
                        )
                    }
                } else {
                    FloatingButtonGroup {
                        NoteColorPickerButton(
                                onClick = onColorPickerClick,
                        )
                    }
                    FloatingButtonGroup {
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
                    }
                }
            }
        }
        FloatingButtonGroup {
            IconButton(
                    modifier = Modifier.focusProperties { canFocus = false },
                    enabled = editorState.canUndo,
                    onClick = editorState::undo,
            ) {
                Icon(
                        imageVector = Icons.Undo,
                        contentDescription = stringResource(Res.string.feature_note_undo),
                )
            }
        }
        FloatingButtonGroup {
            IconButton(
                    modifier = Modifier.focusProperties { canFocus = false },
                    enabled = editorState.canRedo,
                    onClick = editorState::redo,
            ) {
                Icon(
                        imageVector = Icons.Redo,
                        contentDescription = stringResource(Res.string.feature_note_redo),
                )
            }
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
