package com.brody.arxiv.work.summary.converters

import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import java.util.Locale


private const val IMPORTANT_TEXT = "IMPORTANT! Follow next instruction!"

fun createLanguageText(language: SummaryLanguage): String? {
    return if (language != SummaryLanguage.ENGLISH)
        "\n$IMPORTANT_TEXT TRANSLATE to ${language.displayName()} language ALL THE OUTPUT you are going to generate! Every output of yours, every word as to be on language $language."
    else null
}


fun SummaryLanguage.displayName() = this.name.lowercase()
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }