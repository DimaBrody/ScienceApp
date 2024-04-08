package com.brody.arxiv.features.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.designsystem.annotations.DefaultPreview
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.PrimaryHeaderText
import com.brody.arxiv.designsystem.ui.buttons.ArxivSwitch
import com.brody.arxiv.designsystem.ui.list.ArxivListItem
import com.brody.arxiv.features.settings.presentation.models.SettingsGeneralUiModel
import com.brody.arxiv.shared.search.presentation.SelectAiSegmentedButtons

@Composable
fun SettingsScreen() {
    SettingsScreenInternal()
}

@Composable
private fun SettingsScreenInternal(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        AiSettingsItems()

        (uiState as? SettingsUiState.Result)?.let { result ->
            GeneralSettingsItems(
                settingsModel = result.settingsUiModel,
                onUpdateSettings = viewModel::onUpdateValues
            )
        }
    }
}

@Composable
private fun AiSettingsItems() {
    ArxivListItem(
        headlineContent = {
            Text(
                text = "Select AI model", style = PrimaryHeaderText
            )
        }
    )
    SelectAiSegmentedButtons()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GeneralSettingsItems(
    settingsModel: SettingsGeneralUiModel,
    onUpdateSettings: (SettingsGeneralUiModel) -> Unit
) {
    ArxivListItem(
        headlineContent = {
            Text(
                text = "General", style = PrimaryHeaderText
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

@DefaultPreview
@Composable
private fun PreviewItems() {
    ArxivTheme {
//        GeneralSettingsItems(
//            SettingsGeneralUiModel(
//                isSaveSummaries = true,
//                isSavePdfs = true
//            ),
//            onUpdateSettings = {}
//        )
        AiSettingsItems()
    }
}