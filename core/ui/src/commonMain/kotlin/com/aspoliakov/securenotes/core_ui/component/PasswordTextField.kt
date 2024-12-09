package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.common_password_hide
import com.aspoliakov.securenotes.core_ui.resources.common_password_show
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
fun PasswordTextField(
        modifier: Modifier = Modifier,
        password: String,
        onValueChanged: (String) -> Unit,
        errorStringRes: StringResource? = null,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
            modifier = modifier,
            value = password,
            onValueChange = onValueChanged,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
            ),
            supportingText = if (errorStringRes != null) {
                {
                    Text(
                            text = stringResource(errorStringRes),
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                    )
                }
            } else {
                null
            },
            isError = errorStringRes != null,
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Visibility
                } else {
                    Icons.VisibilityOff
                }
                val description = if (passwordVisible) {
                    stringResource(Res.string.common_password_hide)
                } else {
                    stringResource(Res.string.common_password_show)
                }
                IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                ) {
                    Icon(
                            imageVector = image,
                            contentDescription = description,
                    )
                }
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
            ),
            singleLine = true,
    )
}
