package com.brody.arxiv.shared.papers.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.brody.arxiv.core.common.exceptions.NoSavedException
import com.brody.arxiv.core.common.exceptions.OfflineException
import com.brody.arxiv.core.common.typealiases.ScrollListener
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.OnSurface60
import com.brody.arxiv.designsystem.theme.OnSurfaceVariant
import com.brody.arxiv.designsystem.ui.button.PrimaryButton
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.designsystem.ui.list.composableScrollListener
import com.brody.arxiv.designsystem.ui.refresh.ArxivRefresh
import com.brody.arxiv.designsystem.ui.shimmer.shimmerEffect
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.models.presentation.PaperUiCategory
import com.brody.arxiv.shared.papers.models.presentation.PaperUiModel
import com.brody.arxiv.shared.papers.presentation.R
import com.brody.arxiv.shared.saved.models.domain.OnPaperClicked
import com.brody.arxiv.shared.saved.models.domain.toSaveableModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PapersList(
    fetchPapers: FetchPapers,
    onPaperClicked: OnPaperClicked,
    onScrollListener: ScrollListener? = null,
    onOffline: ((Boolean) -> Unit)? = null,
) {
    val isShimmerRequired = fetchPapers !is FetchPapers.Saved
            && fetchPapers !is FetchPapers.Remote.Offline

    PapersListInternal(
        fetchPapers = fetchPapers,
        onPaperClicked = onPaperClicked,
        isShimmerRequired = isShimmerRequired,
        onScrollListener = { onScrollListener?.invoke(it) },
        onOffline = onOffline
    )
}

@Composable
private fun PapersListInternal(
    fetchPapers: FetchPapers,
    isShimmerRequired: Boolean,
    onPaperClicked: OnPaperClicked,
    onScrollListener: ScrollListener,
    onOffline: ((Boolean) -> Unit)? = null,
    papersViewModel: PapersViewModel = hiltViewModel()
) {
    LaunchedEffect(fetchPapers) {
        papersViewModel.getPapers(fetchPapers)
    }

    PapersPagingList(
        isShimmerRequired = isShimmerRequired,
        state = papersViewModel.uiState,
        onPaperClicked = onPaperClicked,
        onScrollListener = onScrollListener,
        onOffline = onOffline,
        saveItem = papersViewModel::saveItem
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PapersPagingList(
    isShimmerRequired: Boolean = true,
    state: () -> StateFlow<PapersUiState>,
    onPaperClicked: OnPaperClicked,
    onScrollListener: ScrollListener,
    onOffline: ((Boolean) -> Unit)? = null,
    saveItem: (String, PaperUiModel?) -> Unit,
) {
    val uiState by state().collectAsStateWithLifecycle()

    val uiConnectedState = uiState as? PapersUiState.Connected

    uiConnectedState?.let { connectedState ->
        Box(Modifier.fillMaxSize()) {
            val pagingItems = connectedState.pagingData.collectAsLazyPagingItems()
//            val hasItems = pagingItems.itemCount > 0
            // refresh, then append
            val loadState = pagingItems.loadState

            var isRefreshing by rememberSaveable { mutableStateOf(false) }
            val refreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = {
                    onOffline?.invoke(false)
                    isRefreshing = true
                    pagingItems.refresh()
                },
            )

            ArxivRefresh(
                isRefreshing = isRefreshing, refreshState = refreshState
            )

//            val connection = remember {
//                object : NestedScrollConnection {
//                    override fun onPreScroll(
//                        available: Offset,
//                        source: NestedScrollSource
//                    ): Offset {
//                        return super.onPreScroll(available, source)
//                    }
//                }
//            }


            val scrollListener = composableScrollListener(onScrollListener)

            LazyColumn(
                userScrollEnabled = loadState.refresh != LoadState.Loading,
                modifier = Modifier
                    .pullRefresh(refreshState)
                    .fillMaxSize(),
                state = scrollListener
            ) {
                if (loadState.refresh is LoadState.Error || loadState.append is LoadState.Error) {
                    isRefreshing = false
                    item {
                        ErrorScreen(
                            loadState = loadState,
                            pagingItems = pagingItems,
                            onOffline = onOffline
                        )
                    }
                } else {
                    if (loadState.refresh != LoadState.Loading) {
                        isRefreshing = false
                        if (pagingItems.itemCount > 0) {
                            items(
                                count = pagingItems.itemCount,
                                key = pagingItems.itemKey { it.id },
//                contentType = pagingItems.itemContentType {}
                            ) { index ->
                                val item = pagingItems[index]
                                item?.let {
                                    PapersItem(
                                        paper = it,
                                        saveItem = saveItem,
                                        onPaperClicked = onPaperClicked
                                    )
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "There are no items",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }

                    } else {
                        items(if (isShimmerRequired) 5 else 0, { it }) {
                            ShimmerList()
                        }
                    }
                }

//                Log.d("HELLO", loadState.toString())
            }
        }
    }
}

@Composable
private fun LazyItemScope.ErrorScreen(
    loadState: CombinedLoadStates,
    pagingItems: LazyPagingItems<PaperUiModel>,
    onOffline: ((Boolean) -> Unit)? = null,
) {
    val error = if (loadState.append is LoadState.Error)
        (loadState.append as LoadState.Error).error
    else (loadState.refresh as LoadState.Error).error

    Column(
        modifier = Modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            16.dp, Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = error.message ?: "Something wrong"
        Text(text = text, style = MaterialTheme.typography.bodyLarge)

        when (error) {
            is OfflineException -> {
                PrimaryButton(
                    text = stringResource(R.string.load_offline),
                    onClick = { onOffline?.invoke(true) }
                )
            }

            is NoSavedException -> {
//                PrimaryButton(
//                    text = stringResource(R.string.load_reload),
//                ) { pagingItems.retry() }
            }

            else -> {
                PrimaryButton(
                    text = stringResource(R.string.load_retry),
                    onClick = { pagingItems.retry() }
                )
            }
        }
    }
}

@Composable
private fun PapersItem(
    paper: PaperUiModel,
    saveItem: (String, PaperUiModel?) -> Unit,
    onPaperClicked: OnPaperClicked
) {
    Column(
        Modifier.clickable(
            onClick = { onPaperClicked(paper.toSaveableModel()) }
        )
    ) {
        Row(
            Modifier.padding(top = 4.dp, end = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 8.dp),
            ) {
                Text(
                    text = paper.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = paper.authors.joinToString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = OnSurface60
                    ),
                    modifier = Modifier.padding(end = 32.dp)
                )
            }
            IconButton(onClick = { saveItem(paper.id, if (!paper.isSaved) paper else null) }) {
                CompositionLocalProvider(LocalContentColor provides OnSurfaceVariant) {
                    Icon(
                        if (paper.isSaved) ArxivIcons.SaveActive else ArxivIcons.SaveInactive,
                        contentDescription = null
                    )
                }
            }
        }
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = paper.summary,
//                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.heightIn(max = 96.dp),
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = paper.updated,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.1.sp)
                )
                val category = paper.primaryCategory
                if (category != null) {
                    Box(Modifier.padding(bottom = 0.5.dp)) {
                        Spacer(
                            Modifier
                                .size(1.dp, 10.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Text(
                        text = category.categoryName,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.1.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerList() {
    Column(Modifier.padding(16.dp, 12.dp)) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(6.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(82.dp)
                .clip(RoundedCornerShape(6.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
@Preview(
    widthDp = 360, heightDp = 212, showBackground = true
)
private fun PapersPreview() {
    ArxivTheme {
        PapersItem(
            paper = PaperUiModel(
                id = "asd",
                updated = "3 Feb 2024",
                published = "4 Feb 2024",
                summary = "We present the second release of the Meta-catalogue of X-Ray detected Clusters of galaxies (hereafter MCXC-II). The MCXC-II has been compiled from publicly available ROSAT All Sky Survey-based (NORAS, REFLEX, BCS, SGP, NEP, MACS, CIZA, RXGCC) and serendipitous (160SD, 400SD, SHARC, WARPS, and EMSS) cluster catalogues We present the second release of the Meta-catalogue of X-Ray detected Clusters of galaxies (hereafter MCXC-II). The MCXC-II has been compiled from publicly available ROSAT All Sky Survey-based (NORAS, REFLEX, BCS, SGP, NEP, MACS, CIZA, RXGCC) and serendipitous (160SD, 400SD, SHARC, WARPS, and EMSS) cluster catalogues...",
                title = "MCXC-II: Second release of the Meta-Catalogue of X-Ray detected here in universe",
                authors = listOf(
                    "T. Sadibekova", "M. Arnaud", "G.W. Pratt", "Soojung Yang", "Juno Nam"
                ),
                doi = "123",
                links = listOf(),
                comment = "Hello",
                isSaved = true,
                categories = listOf(PaperUiCategory("Astronomics", ""))
            ), onPaperClicked = { }, saveItem = { _, _ -> }
        )
    }
}
