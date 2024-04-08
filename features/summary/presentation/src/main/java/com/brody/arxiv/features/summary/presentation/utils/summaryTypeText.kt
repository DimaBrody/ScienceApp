package com.brody.arxiv.features.summary.presentation.utils

import com.brody.arxiv.shared.summary.models.domain.SummaryType

fun SummaryType.toButtonText() = when (this) {
    SummaryType.FAN -> "Fan"
    SummaryType.STUDENT -> "Student"
    SummaryType.EXPERT -> "Expert"
}

fun SummaryType.toDescText() = when(this){
    SummaryType.FAN -> "For curious adventurers"
    SummaryType.STUDENT -> "For student level folks"
    SummaryType.EXPERT -> "Hard things gonna be printed"
}