package com.brody.arxiv.shared.subjects.models.presentation

import androidx.compose.runtime.Stable
import com.brody.arxiv.core.common.models.LinkBits

@Stable
data class SubjectChipData(
    val name: String,
    val link: LinkBits,
) {
    companion object {
        fun default(): SubjectChipData =
            SubjectChipData("Computer Science", LinkBits.extractLinkBits(0))
    }
}