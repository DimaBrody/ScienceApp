package com.brody.arxiv.features.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.designsystem.annotations.DefaultPreview
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.ui.button.ArxivSwitch
import com.brody.arxiv.designsystem.ui.list.ArxivListItem
import com.brody.arxiv.features.settings.presentation.models.SettingsGeneralUiModel
import com.brody.arxiv.features.settings.presentation.models.SettingsUiOption

@Composable
fun SettingsScreen() {
    SettingsScreenInternal()
}

@Composable
private fun SettingsScreenInternal(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    (uiState as? SettingsUiState.Result)?.let { result ->
        GeneralSettingsItems(
            settingsModel = result.settingsUiModel,
            onUpdateSettings = viewModel::onUpdateValues
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GeneralSettingsItems(
    settingsModel: SettingsGeneralUiModel,
    onUpdateSettings: (SettingsGeneralUiModel) -> Unit
) {
    Column {
        ArxivListItem(
            headlineContent = {
                Text(
                    text = "General", style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ), color = MaterialTheme.colorScheme.primary
                )
            }
        )
        val savedInfo = settingsModel.savedInfoList
        for (info in savedInfo) {
            val supportingContent: @Composable () -> Unit = {
                info.subtitle?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
            ArxivListItem(
                headlineContent = {
                    Text(
                        text = info.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                supportingContent = info.subtitle?.let { supportingContent },
                trailingContent = {
                    ArxivSwitch(
                        checked = info.value,
                        onCheckedChange = {
                            info.value = it
                            onUpdateSettings(settingsModel)
                        })
                }
            )
        }
    }
}

@DefaultPreview
@Composable
private fun PreviewItems() {
    ArxivTheme {
        GeneralSettingsItems(
            SettingsGeneralUiModel(
                isSaveSummaries = true,
                isSavePdfs = true
            ),
            onUpdateSettings = {}
        )
    }
}