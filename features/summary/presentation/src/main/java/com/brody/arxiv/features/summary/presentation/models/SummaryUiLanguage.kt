package com.brody.arxiv.features.summary.presentation.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.text.capitalize
import com.brody.arxiv.features.summary.presentation.R
import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import com.brody.arxiv.work.summary.converters.displayName
import java.util.Locale


private val LANG_UKRAINIAN = SummaryLanguage.UKRAINIAN.displayName()
private val LANG_ENGLISH = SummaryLanguage.ENGLISH.displayName()
private val LANG_RUSSIAN = SummaryLanguage.RUSSIAN.displayName()

enum class SummaryUiLanguage(
    @DrawableRes val resource: Int,
    val displayName: String
) {
    ENGLISH(R.drawable.ic_flag_usa, LANG_ENGLISH),
    UKRAINIAN(
        R.drawable.ic_flag_ukraine, LANG_UKRAINIAN
    ),
    RUSSIAN(
        R.drawable.ic_flag_rs, LANG_RUSSIAN
    )
}


fun SummaryLanguage.toUiModel() = SummaryUiLanguage.valueOf(this.name)

fun SummaryUiLanguage.toDomainModel() = SummaryLanguage.valueOf(this.name)