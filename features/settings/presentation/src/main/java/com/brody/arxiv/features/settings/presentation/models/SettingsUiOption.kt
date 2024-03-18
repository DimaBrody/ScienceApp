package com.brody.arxiv.features.settings.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
sealed class SettingsUiOption(
    open val title: String,
    open val subtitle: String? = null,
    open var value: Boolean
) {

    data class SaveSummaries(
        override var value: Boolean
    ) : SettingsUiOption(
        title = "Save created summaries",
        value = value
    )

    data class SavePdfs(
        override var value: Boolean
    ) : SettingsUiOption(
        title = "Save downloaded PDFs",
        subtitle = "Otherwise deleted after a while",
        value = value
    )
}