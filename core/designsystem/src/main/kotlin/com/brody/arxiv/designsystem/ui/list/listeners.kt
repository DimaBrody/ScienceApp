package com.brody.arxiv.designsystem.ui.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

@Composable
fun composableScrollListener(onScrollUpdate: (Int) -> Unit): LazyListState {
    val listState = rememberLazyListState()

    // Remember a flow that emits values of firstVisibleItemScrollOffset from listState
    val scrollOffsetFlow = remember(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
    }

    // Collect from the remembered flow
    LaunchedEffect(listState) {
        scrollOffsetFlow.collect { offset ->
            onScrollUpdate(offset)
        }
    }

    return listState
}
