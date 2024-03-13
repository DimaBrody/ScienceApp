package com.brody.arxiv.features.saved.presentation.ui.navigation

import androidx.compose.runtime.Composable
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.presentation.ui.PapersList

@Composable
fun SavedScreen() {
    PapersList(FetchPapers.Saved)
}