package com.brody.arxiv.shared.subjects.models.presentation

import androidx.compose.runtime.Stable

@Stable
data class SubjectChipData(
    val name: String,
    val link: Int,
) {
    companion object {
        fun default(): SubjectChipData =
            SubjectChipData("Computer Science", 0)
    }
}