package com.brody.arxiv.features.saved.presentation.ui.navigation

import androidx.compose.runtime.Composable
import com.brody.arxiv.core.common.typealiases.ScrollListener
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.presentation.ui.PapersList
import com.brody.arxiv.shared.saved.models.domain.OnPaperClicked

@Composable
fun SavedScreen(
    scrollListener: ScrollListener,
    onPaperClicked: OnPaperClicked
) {
    PapersList(
        fetchPapers = FetchPapers.Saved,
        onScrollListener = scrollListener,
        onPaperClicked = onPaperClicked
    )
}