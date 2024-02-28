package com.brody.arxiv.shared.search.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputFieldHeight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.designsystem.dimens.Dimens.zeroDp
import com.brody.arxiv.designsystem.animations.animateDpAsStateNoLabel
import com.brody.arxiv.designsystem.dimens.Dimens
import com.brody.arxiv.designsystem.extensions.scrollTo
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.OnSurface
import com.brody.arxiv.designsystem.theme.OnSurfaceVariant
import com.brody.arxiv.designsystem.theme.Primary
import com.brody.arxiv.shared.search.models.presentation.SearchCategoriesNode
import com.brody.arxiv.shared.search.models.presentation.findNodeByBits
import com.brody.arxiv.shared.search.presentation.R
import com.brody.arxiv.shared.search.presentation.dimens.SearchDimens
import com.brody.arxiv.shared.search.presentation.dimens.SearchDimens.ActiveSearchPadding
import com.brody.arxiv.shared.search.presentation.dimens.SearchDimens.InactiveSearchPadding
import com.brody.arxiv.shared.subjects.models.domain.LinkBits
import com.brody.arxiv.shared.subjects.models.domain.SubjectType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBarSubjects(
    onCurrentSelectedSubjects: () -> List<Int>,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.subjectsState.collectAsStateWithLifecycle()
    val bottomSheetState by viewModel.sheetUiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Box {
        CustomSearchBar(
            viewModel, pagerState, uiState, coroutineScope, onCurrentSelectedSubjects
        )
        HandlePagerExpanded(viewModel, pagerState, coroutineScope)
    }

    HandleBottomSheet(bottomSheetState, viewModel::dismissBottomSheet)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomSearchBar(
    viewModel: SearchViewModel,
    pagerState: PagerState,
    uiState: SearchSubjectsUiState,
    coroutineScope: CoroutineScope,
    onCurrentSelectedSubjects: () -> List<Int>
) {
    val searchText = stringResource(R.string.search)
    val searchIconText = stringResource(R.string.search_icon)
    val closeIconText = stringResource(R.string.close_icon)

    val animatedPadding by animateDpAsStateNoLabel(
        targetValue = if (viewModel.isSearchActive) InactiveSearchPadding else ActiveSearchPadding,
    )

    SearchBar(
        query = viewModel.searchQuery,
        onQueryChange = viewModel::onSearchQueryChange,
        onSearch = {
            viewModel.updateActive(false)
        },
        active = viewModel.isSearchActive,
        onActiveChange = { isActive ->
            viewModel.updateActive(isActive, onCurrentSelectedSubjects())
        },
        enabled = viewModel.pagerExpandedTitle.isNullOrEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = animatedPadding, end = animatedPadding, top = animatedPadding / 2
            ),
        placeholder = { Text(searchText) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = searchIconText)
        },
        trailingIcon = {
            if (viewModel.isSearchActive) {
                Icon(
                    modifier = Modifier.clickable(onClick = viewModel::handleSearchClose),
                    imageVector = Icons.Default.Close,
                    contentDescription = closeIconText
                )
            }
        },
        tonalElevation = zeroDp,
        colors = getSearchBarColors()
    ) {
        if (uiState is SearchSubjectsUiState.Active) {
            val uiStateActive = uiState as SearchSubjectsUiState.Active
            PagerWithSearchedItems(
                uiState = uiStateActive,
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                onPagerChanged = viewModel::updatePagerExpanded,
                onSelectionToggle = viewModel::toggleSelection,
                openBottomSheetDialog = viewModel::openBottomSheet,
            ) { pagerState.scrollTo(coroutineScope, 0, false) }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HandlePagerExpanded(
    viewModel: SearchViewModel, pagerState: PagerState, coroutineScope: CoroutineScope
) {
    viewModel.pagerExpandedTitle?.let { title ->
        SearchPagerExpanded(title) {
            viewModel.closePagerExpanded(pagerState.currentPage == 1)
            pagerState.scrollTo(coroutineScope, pagerState.currentPage - 1)
        }
    }
}

@Composable
fun HandleBottomSheet(state: BottomSheetUiState?, onDismiss: () -> Unit) {
    (state as? BottomSheetUiState.Active)?.let {
        BottomSheet(it.data, onDismiss)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerWithSearchedItems(
    uiState: SearchSubjectsUiState.Active,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    onPagerChanged: (String?) -> Unit,
    onSelectionToggle: (SearchCategoriesNode) -> Unit,
    openBottomSheetDialog: ((SearchCategoriesNode) -> Unit)?,
    onPagerDisposed: () -> Unit
) {
    var page1Node by rememberSaveable { mutableStateOf<LinkBits?>(null) }
    var page2Node by rememberSaveable { mutableStateOf<LinkBits?>(null) }

    DisposableEffect(Unit) {
        onDispose(onPagerDisposed)
    }

    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top,
        userScrollEnabled = false // Disable swipe
    ) { page ->
        val subjectsHierarchy = uiState.updatedMap

        when (page) {
            0 -> SearchList(
                searchHierarchy = subjectsHierarchy, onSelectionToggle = onSelectionToggle
            ) {
                onPagerChanged(it.name)
                page1Node = LinkBits.extractLinkBits(it.link)
                pagerState.scrollTo(coroutineScope, 1)
            }

            1 -> SearchList(
                searchHierarchy = subjectsHierarchy.findNodeByBits(page1Node)?.childrenNodes,
                onSelectionToggle = onSelectionToggle,
                openBottomDialog = openBottomSheetDialog
            ) {
                onPagerChanged(it.name)
                page2Node = LinkBits.extractLinkBits(it.link)
                pagerState.scrollTo(coroutineScope, 2)
            }

            2 -> SearchList(
                searchHierarchy = subjectsHierarchy.findNodeByBits(page2Node)?.childrenNodes,
                onSelectionToggle = onSelectionToggle,
                openBottomDialog = openBottomSheetDialog
            )
        }
    }
}

@Composable
fun SearchList(
    searchHierarchy: Map<Int, SearchCategoriesNode>?,
    onSelectionToggle: (SearchCategoriesNode) -> Unit,
    openBottomDialog: ((SearchCategoriesNode) -> Unit)? = null,
    onItemClicked: ((SearchCategoriesNode) -> Unit)? = null,
) {
    searchHierarchy?.values?.let {
        LazyColumn {
            items(items = it.toList()) { node ->
                SearchListItem(
                    node = node,
                    onSelectionToggle = onSelectionToggle,
                    onItemClicked = onItemClicked,
                    openBottomDialog = openBottomDialog
                )
            }
        }
    }
}

@Composable
fun SearchListItem(
    node: SearchCategoriesNode,
    onSelectionToggle: (SearchCategoriesNode) -> Unit,
    onItemClicked: ((SearchCategoriesNode) -> Unit)?,
    openBottomDialog: ((SearchCategoriesNode) -> Unit)?
) {
    val childCount = node.childCount

    ListItem(
        modifier = Modifier.clickable(
            enabled = childCount > 0
        ) {
            node.childrenNodes?.let {
                onItemClicked?.invoke(node)
            }
        },
        headlineContent = { Text(node.name, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(0.9f)
            ) {
                CompositionLocalProvider(LocalContentColor provides OnSurfaceVariant) {
                    if (node.type != SubjectType.SUBJECT) {
                        IconButton(onClick = { openBottomDialog?.invoke(node) }) {
                            Icon(
                                Icons.Outlined.Info,
                                contentDescription = null,
                            )
                        }
                    }
                    if (childCount > 0) {
                        Text(
                            node.childCount.toString(),
                            fontWeight = FontWeight.Medium,
                            fontSize = SearchDimens.countTextSize
                        )
                        Spacer(Modifier.width(Dimens.spacingTiny))
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        leadingContent = {
            TriStateCheckbox(
                state = node.toggleableState, onClick = {
                    onSelectionToggle(node)
                }, colors = CheckboxDefaults.colors(
                    checkedColor = Primary
                )
            )
        },
        colors = ListItemDefaults.colors(
            headlineColor = OnSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(data: BottomSheetData, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
    ) {
        ModalSheetContent(data = data)
    }
}

@Composable
fun ModalSheetContent(data: BottomSheetData) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingNormal),
        modifier = Modifier.padding(SearchDimens.bsContentPaddings)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f, false)
            )
            Text(
                text = "(${data.id})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = SearchDimens.bsSmallText)
            )
        }
        Text(
            text = data.desc,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchPagerExpanded(
    title: String, onBackPressed: () -> Unit
) {

    val density = LocalDensity.current
    val insects = SearchBarDefaults.windowInsets

    BackHandler(onBack = onBackPressed)
    Surface(
        modifier = Modifier
            .zIndex(2f)
            .padding(
                top = insects
                    .asPaddingValues(density)
                    .calculateTopPadding() + SearchDimens.searchBarVerticalPadding
            ),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(InputFieldHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(
                    end = SearchDimens.searchTitleTextPaddingEnd
                )
            )
        }
    }
}


@Preview(
    widthDp = 320, heightDp = 240, backgroundColor = 0xFFFFF4F3
)
@Composable
fun SearchItemPreview() {
    val data = BottomSheetData("Biomoleculsdafsfdasdfsafdsaes", "q-bio.BM", "How are you doing?")

    ArxivTheme {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
//            ModalSheetContent(data = data)
            SearchPagerExpanded("Physasdfasfadsfsdafasdasdfics") {}
//            SearchListItem(
//                node = SearchCategoriesNode(
//                    "ai.num",
//                    0,
//                    0,
//                    "Artificial Intelligence",
//                    SubjectType.CATEGORY,
//                    SelectedType.SELECTED,
//                    null
//                ), onSelectionToggle = {}, null, null
//            )
        }
//        CompositionLocalProvider(LocalContentColor provides OnSurfaceVariant) {
//            IconButton(onClick = { }) {
//                Icon(
//                    Icons.Outlined.Info,
//                    contentDescription = null,
////                    tint = OnSurfaceVariant
//                )
//            }
//        }
    }

}