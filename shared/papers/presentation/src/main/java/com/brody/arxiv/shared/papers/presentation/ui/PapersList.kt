package com.brody.arxiv.shared.papers.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.OnSurface60
import com.brody.arxiv.designsystem.theme.OnSurfaceVariant
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.designsystem.ui.shimmer.shimmerEffect
import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.models.presentation.PaperUiModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PapersList(
    fetchPapers: FetchPapers
) {
    val isShimmerRequired = fetchPapers !is FetchPapers.Saved
    PapersListInternal(fetchPapers, isShimmerRequired)
}

@Composable
private fun PapersListInternal(
    fetchPapers: FetchPapers,
    isShimmerRequired: Boolean,
    papersViewModel: PapersViewModel = hiltViewModel()
) {
    papersViewModel.getPapers(fetchPapers)

    PapersPagingList(
        isShimmerRequired = isShimmerRequired,
        state = papersViewModel.uiState,
        saveItem = papersViewModel::saveItem
    )
}

@Composable
private fun PapersPagingList(
    isShimmerRequired: Boolean = true,
    state: () -> StateFlow<PapersUiState>,
    saveItem: (String, PaperUiModel?) -> Unit
) {
    val uiState by state().collectAsStateWithLifecycle()

    val uiConnectedState = uiState as? PapersUiState.Connected

    uiConnectedState?.let { connectedState ->
        val pagingItems = connectedState.pagingData.collectAsLazyPagingItems()
        val hasItems = pagingItems.itemCount > 0

        LazyColumn(userScrollEnabled = hasItems) {
            if (hasItems) {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { it.id },
//                contentType = pagingItems.itemContentType {}
                ) { index ->
                    val item = pagingItems[index]
                    item?.let {
                        PapersItem(paper = it, saveItem)
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            } else {
                items(if (isShimmerRequired) 5 else 0, { it }) {
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
            }


            val loadState = pagingItems.loadState
            Log.d("HELLO", loadState.toString())

        }
    }

}

@Composable
private fun PapersItem(paper: PaperUiModel, saveItem: (String, PaperUiModel?) -> Unit) {
    Column {
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
                val category = paper.category
                if(category.isNotEmpty()){
                    Box(Modifier.padding(bottom = 0.5.dp)) {
                        Spacer(
                            Modifier
                                .size(1.dp, 10.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Text(
                        text = category,
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
                categoryId = "sss",
                category = "Astronomics"
            )
        ) { _, _ -> }
    }
}
