package com.brody.arxiv.shared.search.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.ui.showToast
import com.brody.arxiv.designsystem.annotations.DefaultPreview
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.ui.buttons.ArxivSegmentedButton
import com.brody.arxiv.designsystem.ui.buttons.ArxivSegmentedButtonDefaults
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.shared.search.presentation.utils.getPainterResource
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import com.brody.arxiv.shared.settings.ai.models.presentation.LanguageUiModel
import com.brody.arxiv.shared.settings.ai.models.presentation.toPresentationModel

@Composable
fun SelectAiSegmentedButtons() {
    SelectAiSegmentedButtonInternal()
    SettingsAiDialog()
}

@Composable
internal fun SelectAiSegmentedButtonInternal(
    settingsAiViewModel: SettingsAiViewModel = hiltViewModel()
) {
    val uiState = settingsAiViewModel.settingsAiState.collectAsStateWithLifecycle()

    (uiState.value as? SettingsAiUiState.Data)?.settingsAiDataModel?.let { settingsModel ->

        SingleAiSegmentedButton(
            settingsModel = settingsModel,
            onModelUpdate = settingsAiViewModel::updateSelectedModel,
            onSelectModelClick = settingsAiViewModel::openDialog
        )
    }
}

@Composable
private fun SingleAiSegmentedButton(
    settingsModel: SettingsAiDataModel,
    onModelUpdate: (LanguageUiModel) -> Unit,
    onSelectModelClick: (LanguageUiModel) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        settingsModel.modelsConfig.entries.forEachIndexed { index, modelConfigEntry ->
            val languageModel = modelConfigEntry.key
            if (languageModel == LanguageModel.NOTHING) return@forEachIndexed

            val languageUiModel = languageModel.toPresentationModel()
            val modelConfig = modelConfigEntry.value
            val hasKey = modelConfig.hasKey
            val context = LocalContext.current
            val isSelected = languageUiModel.name == settingsModel.selectedModel.name

            ArxivSegmentedButton(index = index,
                count = LanguageUiModel.entries.size,
                selected = isSelected,
                text = languageUiModel.uiName(),
                isHighlight = hasKey,
                icon = {
                    val iconSource = if (isSelected) {
                        IconSource.Vector(ArxivIcons.Check) // Assuming ArxivIcons.Check is an ImageVector
                    } else {
                        IconSource.Drawable(languageUiModel.getPainterResource()) // Assuming this returns a Painter
                    }

                    IconFromSource(
                        iconSource = iconSource,
                        contentDescription = null, // Adjust as needed
                        tint = ArxivSegmentedButtonDefaults.getHighlightInactiveContentColor(
                            isHighlight = isSelected
                        )
                    )
                }) {
                if (hasKey) onModelUpdate(languageUiModel)
                else {
                    if (languageUiModel == LanguageUiModel.CUSTOM) {
                        showToast(context, "Functionality will be added soon")
                    } else {
                        onSelectModelClick(languageUiModel)
                    }
                }
            }
        }

    }
}

// Define a sealed class to represent the icon source
sealed class IconSource {
    data class Vector(val imageVector: ImageVector) : IconSource()
    data class Drawable(val painter: Painter) : IconSource()
}

// Helper composable to render the icon from either source
@Composable
fun IconFromSource(
    iconSource: IconSource,
    contentDescription: String?,
    tint: Color
) {
    when (iconSource) {
        is IconSource.Vector -> Icon(
            imageVector = iconSource.imageVector,
            contentDescription = contentDescription,
            tint = tint
        )

        is IconSource.Drawable -> Icon(
            painter = iconSource.painter,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}


@DefaultPreview
@Composable
fun PreviewSegmented() {
    ArxivTheme {
        SingleAiSegmentedButton(
            settingsModel = SettingsAiDataModel(
                LanguageModel.GEMINI, mapOf(
                    LanguageModel.GEMINI to LanguageModelConfig(),
                    LanguageModel.OPENAI to LanguageModelConfig(hasKey = true),
                    LanguageModel.CUSTOM to LanguageModelConfig()
                )
            ),
            onModelUpdate = {},
            onSelectModelClick = {}
        )
    }
}