package com.brody.arxiv.designsystem.extensions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.scrollTo(scope: CoroutineScope, page: Int, isAnimated: Boolean = true) {
    scope.launch {
        if (isAnimated) animateScrollToPage(page) else scrollToPage(page)
    }
}