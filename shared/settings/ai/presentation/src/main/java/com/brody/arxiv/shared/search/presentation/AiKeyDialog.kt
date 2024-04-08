package com.brody.arxiv.shared.search.presentation

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.extensions.toInt
import com.brody.arxiv.designsystem.annotations.DefaultPreview
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.Primary
import com.brody.arxiv.designsystem.ui.buttons.DefaultTextButton
import com.brody.arxiv.designsystem.ui.buttons.PrimaryButton
import com.brody.arxiv.designsystem.ui.buttons.SecondaryButton
import com.brody.arxiv.designsystem.ui.dialogs.ArxivAlertDialog
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.shared.search.presentation.utils.getPainterResource
import com.brody.arxiv.shared.settings.ai.models.presentation.LanguageUiModel
import com.brody.arxiv.shared.settings.ai.presentation.R


@Composable
internal fun SettingsAiDialog() {
    SettingsAiDialogInternal()
}

@Composable
internal fun SettingsAiDialogInternal(
    viewModel: SettingsAiViewModel = hiltViewModel()
) {
    val dialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()

    if (dialogUiState !is SettingsAiDialogUiState.Idle) {
        val languageUiModel = dialogUiState.model!!

        ArxivAlertDialog(
            onDismissRequest = viewModel::closeDialog
        ) {
            DialogContent(
                languageUiModel = languageUiModel,
                dialogUiState = dialogUiState,
                inputText = viewModel::dialogInputText.get(),
                onTextChanged = viewModel::dialogInputText::set,
                onTrySaveKey = viewModel::trySetNewKey,
                onDismiss = viewModel::closeDialog
            )
        }
    }
}

@Composable
private fun DialogContent(
    languageUiModel: LanguageUiModel,
    dialogUiState: SettingsAiDialogUiState,
    inputText: String?,
    onTextChanged: (String) -> Unit,
    onTrySaveKey: (LanguageUiModel) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = languageUiModel.getPainterResource(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = languageUiModel.uiName(), style = MaterialTheme.typography.titleLarge
            )

        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Paste your API key (more info where to find it here)",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))
        DialogTextField(
            state = dialogUiState,
            inputText = inputText,
            onTextChanged = onTextChanged,
            onTrySaveKey = onTrySaveKey
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel", color = MaterialTheme.colorScheme.primary
                )
            }

            val isChickEnabled =
                (dialogUiState is SettingsAiDialogUiState.Editing && dialogUiState.isEnabled)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(56.dp)
            ) {
                if (dialogUiState is SettingsAiDialogUiState.Idle && dialogUiState.isNewKey) {
                    Icon(imageVector = ArxivIcons.Check, contentDescription = null)
                } else {
                    when (dialogUiState) {
                        is SettingsAiDialogUiState.Inserting.Progress -> {
                            CircularProgressIndicator(
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp,
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(10.dp)
                            )
                        }

                        is SettingsAiDialogUiState.Inserting.Success -> {
                            onDismiss()
                        }

                        else -> TextButton(
                            modifier = Modifier.alpha(0.38f + isChickEnabled.toInt() * 0.62f),
                            enabled = isChickEnabled,
                            onClick = { onTrySaveKey(dialogUiState.model!!) }) {
                            Text(text = "Save", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun DialogTextField(
    state: SettingsAiDialogUiState,
    inputText: String?,
    onTextChanged: (String) -> Unit,
    onTrySaveKey: (LanguageUiModel) -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = inputText.orEmpty(),
        onValueChange = onTextChanged,
        placeholder = { Text(text = "API key") },
        maxLines = 1,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image =
                if (passwordVisible) ArxivIcons.PasswordHidden else ArxivIcons.PasswordVisible
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onTrySaveKey(state.model!!)
        }),
        isError = state is SettingsAiDialogUiState.Inserting.Failed,
        supportingText = {
            Box(Modifier.height(16.dp), contentAlignment = Alignment.CenterStart) {
                if (state is SettingsAiDialogUiState.Inserting.Failed) {

                    Text(
                        text = "The API key you entered is not valid",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


@DefaultPreview
@Composable
fun DialogPreviewSegmented() {
    ArxivTheme {
        DialogContent(
            languageUiModel = LanguageUiModel.GEMINI,
            dialogUiState = SettingsAiDialogUiState.Editing(LanguageUiModel.GEMINI, "Hello", ""),
//            dialogUiState = SettingsAiDialogUiState.Inserting.Progress(LanguageUiModel.GEMINI),
            onTextChanged = {},
            onTrySaveKey = {},
            onDismiss = {},
            inputText = "Hello"
        )
    }
}