package com.aspoliakov.securenotes.feature_keys.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.ButtonWithLoader
import com.aspoliakov.securenotes.core_ui.component.PasswordTextField
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.app_name
import com.aspoliakov.securenotes.core_ui.resources.common_apply
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_password_hint
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_password_requirement_capital_letter
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_password_requirement_digit
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_password_requirement_length
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_password_requirement_letter
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_subtitle
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
internal fun KeysCreatingView(
        modifier: Modifier = Modifier,
        state: KeysState.Creating,
        intentHandler: (KeysIntent) -> Unit = {},
) {
    Column(
            modifier = modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KeysHeader(
                titleRes = Res.string.feature_keys_title,
                titleArg = stringResource(Res.string.app_name),
                subtitleRes = Res.string.feature_keys_subtitle,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(24.dp),
        ) {
            PasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    password = state.password,
                    onValueChanged = { intentHandler(KeysIntent.OnPasswordChanged(it)) },
                    labelStringRes = Res.string.feature_auth_password_hint,
                    errorStringRes = (state.actionState as? KeysActionState.Error)?.error?.res,
            )
            PasswordRequirementsView(
                    modifier = Modifier.padding(top = 16.dp),
                    state = state,
            )
            if (state.actionState !is KeysActionState.Completed) {
                Spacer(modifier = Modifier.height(24.dp))
                ButtonWithLoader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        onClick = { intentHandler.invoke(KeysIntent.OnApplyClick) },
                        isLoading = state.actionState is KeysActionState.Loading,
                        stringResource = Res.string.common_apply,
                )
            }
        }
    }
}

@Composable
internal fun PasswordRequirementsView(
        modifier: Modifier = Modifier,
        state: KeysState.Creating,
) {
    Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
    ) {
        PasswordRequirementItem(
                text = Res.string.feature_keys_password_requirement_length,
                success = state.passwordRequirements.maxLength,
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordRequirementItem(
                text = Res.string.feature_keys_password_requirement_digit,
                success = state.passwordRequirements.oneDigit,
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordRequirementItem(
                text = Res.string.feature_keys_password_requirement_letter,
                success = state.passwordRequirements.oneLetter,
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordRequirementItem(
                text = Res.string.feature_keys_password_requirement_capital_letter,
                success = state.passwordRequirements.oneCapitalLetter,
        )
    }
}

@Composable
internal fun PasswordRequirementItem(
        modifier: Modifier = Modifier,
        text: StringResource,
        success: Boolean,
) {
    val contentColor = if (success) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
                modifier = Modifier.size(16.dp),
                imageVector = if (success) Icons.Checked else Icons.Unchecked,
                contentDescription = stringResource(text),
                tint = if (success) MaterialTheme.colorScheme.primary else contentColor,
        )
        Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(text),
                color = contentColor,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
        )
    }
}
