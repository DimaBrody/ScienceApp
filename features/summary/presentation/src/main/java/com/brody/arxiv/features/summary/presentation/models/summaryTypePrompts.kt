package com.brody.arxiv.features.summary.presentation.models

import com.brody.arxiv.shared.summary.models.domain.SummaryType

//private const val START_MESSAGE = "You are expert in discussed field."
//private const val START_MESSAGE = "WRITE OUTPUT IN MARKDOWN. MARKDOWN."
private const val START_MESSAGE = ""

private const val SYSTEM_MESSAGE_FAN = """
You are teacher who wants to describe it as easy as possible. 
Describe it as to person who is curious and want details and intuitive explanation but does not know the theme at all.
IMPORTANT! Don't write hard and unique scientific words and formulas or if must have - describe them as for child.
"""

private const val SYSTEM_MESSAGE_STUDENT = """
Describe it as to student, who knows basics but still needs intuitive explanation of hard things. 
"""

private const val SYSTEM_MESSAGE_EXPERT = """
You are expert in discussed field. Summarize it in scientific language as to other expert. Use logic and concepts, less literature text. Use all small scientific details. ALL SCIENTIFIC DETAILS.
"""

fun SummaryType.toSystemMessage() = when (this) {
    SummaryType.FAN -> START_MESSAGE + SYSTEM_MESSAGE_FAN
    SummaryType.STUDENT -> START_MESSAGE + SYSTEM_MESSAGE_STUDENT
    SummaryType.EXPERT -> START_MESSAGE + SYSTEM_MESSAGE_EXPERT
}