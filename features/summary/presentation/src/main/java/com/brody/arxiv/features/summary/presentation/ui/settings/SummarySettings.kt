package com.brody.arxiv.features.summary.presentation.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.extensions.toInt
import com.brody.arxiv.core.common.utils.round
import com.brody.arxiv.designsystem.annotations.DefaultPreview
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.SecondaryHeaderText
import com.brody.arxiv.designsystem.ui.appbar.ArxivTopAppBar
import com.brody.arxiv.designsystem.ui.buttons.ArxivSwitch
import com.brody.arxiv.designsystem.ui.dialogs.AnimatedFullScreenDialog
import com.brody.arxiv.designsystem.ui.dialogs.ArxivAlertDialog
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.designsystem.ui.list.ArxivListItem
import com.brody.arxiv.designsystem.ui.menu.ArxivDropdownMenuBox
import com.brody.arxiv.features.summary.presentation.models.SettingsSection
import com.brody.arxiv.features.summary.presentation.models.SummaryUiLanguage
import com.brody.arxiv.shared.search.presentation.SelectAiSegmentedButtons
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun SummarySettingsDialog(
    isShowDialog: MutableState<Boolean>,
) {
    val onDismissRequest = { isShowDialog.value = false }
    AnimatedFullScreenDialog(
        expanded = isShowDialog.value, onDismissRequest = onDismissRequest
    ) {
        SettingsDialogContent(
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
private fun SettingsDialogContent(
    onDismissRequest: () -> Unit, viewModel: SummarySettingsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ArxivTopAppBar(
                title = "Summary settings",
                actionIcons = listOf(ArxivIcons.Close),
                onActionClicks = listOf(onDismissRequest),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.fillMaxHeight()
    ) { padding ->
        Column(
            Modifier.padding(padding)
        ) {
            val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

            SelectModelSection()
            ModelConfigSettings(
                settingsUiState = settingsUiState,
                onTemperatureUpdate = viewModel::setTemperature,
                onToggleStream = viewModel::toggleStream,
                onLanguageSelect = viewModel::updateLanguage,
                summaryLanguageFlow = viewModel::summarySettings::get
            )
        }
    }
}

@Composable
private fun SelectModelSection() {
    ArxivListItem(headlineContent = {
        Text(
            text = "Select AI model",
            style = SecondaryHeaderText,
        )
    }, isSecondary = true)
    SelectAiSegmentedButtons()
}

@Composable
private fun ModelConfigSettings(
    settingsUiState: SummarySettingsAiUiState,
    onTemperatureUpdate: (Float) -> Unit,
    onToggleStream: () -> Unit,
    onLanguageSelect: (SummaryUiLanguage) -> Unit,
    summaryLanguageFlow: () -> StateFlow<SummarySettingsLanguage>,
) {
    var lastClickedSection: SettingsSection? by remember { mutableStateOf(null) }

    if (lastClickedSection != null) {
        ArxivAlertDialog(onDismissRequest = { lastClickedSection = null }) {
            AlertDialogContent(section = lastClickedSection!!)
        }
    }
    val onInfoClick: (SettingsSection) -> Unit = { lastClickedSection = it }

    val settingsConfigUiState = settingsUiState as? SummarySettingsAiUiState.Fetched

    val isEnabled = settingsConfigUiState != null
    Column(
        modifier = Modifier
            .alpha(0.38f + (isEnabled).toInt() * 0.62f)
            .then(if (isEnabled) Modifier else Modifier.clickable(enabled = false) {})
    ) {
        val modelConfig = settingsConfigUiState?.languageModelConfig

        LanguageSection(
            summaryLanguageFlow = summaryLanguageFlow, onLanguageSelect = onLanguageSelect
        )
        TemperatureSection(
            modelConfig = modelConfig,
            onTemperatureChange = onTemperatureUpdate,
            onInfoClick = onInfoClick
        )
        StreamSection(
            modelConfig = modelConfig, onToggleStream = onToggleStream, onInfoClick = onInfoClick
        )
    }
}

@Composable
private fun TemperatureSection(
    modelConfig: LanguageModelConfig?,
    onTemperatureChange: (Float) -> Unit,
    onInfoClick: (SettingsSection) -> Unit
) {
    var currentTemperature: Float? by remember { mutableStateOf(null) }
    val temperature = currentTemperature ?: modelConfig?.temperature ?: 0f

    ItemWithInfo(
        section = SettingsSection.TEMPERATURE,
        onInfoClick = onInfoClick
    ) {
        Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.width(52.dp)) {
            Text(
                text = temperature.round(1),
                style = SecondaryHeaderText,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }

    val isEnabled = modelConfig != null

    SliderWithLabelAboveThumb(
        isEnabled = isEnabled,
        temperature = temperature,
        onTemperatureUpdate = {
            currentTemperature = it
        },
        onTemperatureChange = onTemperatureChange
    )
}

@Composable
fun SliderWithLabelAboveThumb(
    isEnabled: Boolean,
    temperature: Float,
    onTemperatureUpdate: (Float) -> Unit,
    onTemperatureChange: (Float) -> Unit
) {
    Slider(
        valueRange = 0f..2f,
//        steps = 9,
        value = temperature,
        onValueChange = {
            if (isEnabled) { // Only allow updates if modelConfig is not null
                onTemperatureUpdate(it)
            }
        },
        onValueChangeFinished = {
            if (isEnabled)
                onTemperatureChange(temperature)
        },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        enabled = isEnabled,
        modifier = Modifier
            .padding(horizontal = 20.dp)
    )
}

@Composable
private fun StreamSection(
    modelConfig: LanguageModelConfig?,
    onToggleStream: () -> Unit,
    onInfoClick: (SettingsSection) -> Unit
) {
    ItemWithInfo(
        section = SettingsSection.STREAM, onInfoClick = onInfoClick
    ) {
        ArxivSwitch(
            checked = modelConfig?.isStream ?: true,
            onCheckedChange = { onToggleStream() },
            enabled = modelConfig != null
        )
    }
}

@Composable
private fun LanguageSection(
    summaryLanguageFlow: () -> StateFlow<SummarySettingsLanguage>,
    onLanguageSelect: (SummaryUiLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ArxivDropdownMenuBox(expanded = expanded, onExpandedChange = {
        expanded = it
    }) {
        ItemWithInfo(
            section = SettingsSection.LANGUAGE,
            modifier = Modifier.menuAnchor()
        ) {
            Box(
                Modifier
                    .width(52.dp)
                    .padding(end = 8.dp), contentAlignment = Alignment.CenterEnd
            ) {
                val summaryLanguage by summaryLanguageFlow().collectAsStateWithLifecycle()

                (summaryLanguage as? SummarySettingsLanguage.Fetched)?.let {
                    FlagComposable(
                        language = it.language
                    )
                }

            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)),
            onDismissRequest = { expanded = false },
        ) {
            SummaryUiLanguage.entries.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.displayName) },
                    trailingIcon = { FlagComposable(language) },
                    onClick = {
                        onLanguageSelect(language)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
private fun FlagComposable(
    language: SummaryUiLanguage,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(language.resource),
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .size(26.dp, 19.dp),
        contentDescription = null,
    )
}


@Composable
private fun ItemWithInfo(
    section: SettingsSection,
    modifier: Modifier = Modifier,
    onInfoClick: ((SettingsSection) -> Unit)? = null,
    enabled: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ArxivListItem(headlineContent = {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.text,
                style = SecondaryHeaderText,
            )
            if (section.description != null && onInfoClick != null) {
                IconButton(onClick = { onInfoClick(section) }, enabled = enabled) {
                    Icon(
                        imageVector = ArxivIcons.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }, trailingContent = trailingContent, isSecondary = true, modifier = modifier.height(56.dp))

}

@Composable
private fun AlertDialogContent(
    section: SettingsSection
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 24.dp)
            .padding(horizontal = 24.dp),
    ) {
        Text(
            text = section.text, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp)
        )
        Text(text = section.description.orEmpty(), style = MaterialTheme.typography.bodyMedium)
    }
}


@DefaultPreview
@Composable
private fun SettingsPreview() {
    ArxivTheme {
        Column {
//            ArxivAlertDialog(onDismissRequest = { }) {
//                AlertDialogContent(section = SettingsSection.STREAM)
//            }
//            FlagComposable(SummaryUiLanguage.UKRAINIAN)
            Box(contentAlignment = Alignment.Center) {
                Text(text = "1.2", style = SecondaryHeaderText)
            }
        }
    }
}