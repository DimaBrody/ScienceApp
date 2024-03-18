package com.brody.arxiv.shared.subjects.presentation.ui.list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.core.common.models.LinkBits
import com.brody.arxiv.designsystem.dimens.Dimens
import com.brody.arxiv.designsystem.extensions.scrollTo
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.designsystem.theme.OnSurface
import com.brody.arxiv.designsystem.theme.OnSurfaceVariant
import com.brody.arxiv.designsystem.theme.OnboardingSmallTheme
import com.brody.arxiv.designsystem.theme.Primary
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.shared.search.models.presentation.SearchState
import com.brody.arxiv.shared.subjects.models.domain.SubjectType
import com.brody.arxiv.shared.subjects.models.presentation.CategoriesNodeUiModel
import com.brody.arxiv.shared.subjects.models.presentation.findNodeByBits
import com.brody.arxiv.shared.subjects.presentation.dimens.SubjectsDimens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectsList(
    searchFlow: StateFlow<SearchState>,
    onGetCurrentChips: (() -> List<Int>)? = null,
    isAlwaysActive: Boolean = false,
    isSearchActive: Boolean? = null,
    viewModel: SubjectsViewModel = hiltViewModel()
) {
    Log.d("HELLOLIST", "VM: $viewModel")

    viewModel.setOnGetCurrentChips(onGetCurrentChips)

    val uiState by viewModel.getSubjectsState(searchFlow, isAlwaysActive)
        .collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(viewModel.pagerState)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isSearchActive) {
        if (isSearchActive == true)
            viewModel.setUpdateRequired(true)
    }

//    HandlePagerExpanded(viewModel, pagerState, coroutineScope)

    PagerWithSearchedItems(
        uiState = uiState,
        pagerState = pagerState,
        coroutineScope = coroutineScope,
        isAlwaysActive = isAlwaysActive,
        onPagerChanged = viewModel::updatePagerExpanded,
        onSelectionToggle = viewModel::toggleSelection,
        openBottomSheetDialog = viewModel::openBottomSheet,
    ) { pagerState.scrollTo(coroutineScope, 0, false) }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HandlePagerExpanded(
    viewModel: SubjectsViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(viewModel.pagerState)

    viewModel.pagerExpandedTitle?.let { title ->
        SearchPagerExpanded(title) {
            viewModel.closePagerExpanded(pagerState.currentPage == 1)
            pagerState.scrollTo(coroutineScope, pagerState.currentPage - 1)
        }
    }
}


@Composable
fun HandleBottomSheet(
) {
    val viewModel: SubjectsViewModel = hiltViewModel()
    val bottomSheetState by viewModel.sheetUiState.collectAsStateWithLifecycle()
    Log.d("HELLOBS", "$bottomSheetState, VM: $viewModel")
    (bottomSheetState as? BottomSheetUiState.Active)?.let {
        BottomSheet(it.data, viewModel::dismissBottomSheet)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerWithSearchedItems(
    uiState: SubjectsUiState,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    isAlwaysActive: Boolean,
    onPagerChanged: (String?) -> Unit,
    onSelectionToggle: (CategoriesNodeUiModel) -> Unit,
    openBottomSheetDialog: ((CategoriesNodeUiModel) -> Unit)?,
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
                searchHierarchy = subjectsHierarchy,
                isAlwaysActive = isAlwaysActive,
                onSelectionToggle = onSelectionToggle
            ) {
                onPagerChanged(it.name)
                page1Node = LinkBits.extractLinkBits(it.link)
                pagerState.scrollTo(coroutineScope, 1)
            }

            1 -> SearchList(
                searchHierarchy = subjectsHierarchy?.findNodeByBits(page1Node)?.childrenNodes,
                isAlwaysActive = isAlwaysActive,
                onSelectionToggle = onSelectionToggle,
                openBottomDialog = openBottomSheetDialog
            ) {
                onPagerChanged(it.name)
                page2Node = LinkBits.extractLinkBits(it.link)
                pagerState.scrollTo(coroutineScope, 2)
            }

            2 -> SearchList(
                searchHierarchy = subjectsHierarchy?.findNodeByBits(page2Node)?.childrenNodes,
                isAlwaysActive = isAlwaysActive,
                onSelectionToggle = onSelectionToggle,
                openBottomDialog = openBottomSheetDialog
            )
        }
    }
}

@Composable
private fun SearchList(
    searchHierarchy: Map<Int, CategoriesNodeUiModel>?,
    isAlwaysActive: Boolean,
    onSelectionToggle: (CategoriesNodeUiModel) -> Unit,
    openBottomDialog: ((CategoriesNodeUiModel) -> Unit)? = null,
    onItemClicked: ((CategoriesNodeUiModel) -> Unit)? = null,
) {
    searchHierarchy?.values?.let {
        LazyColumn {
            items(items = it.toList()) { node ->
                SearchListItem(
                    node = node,
                    isAlwaysActive = isAlwaysActive,
                    onSelectionToggle = onSelectionToggle,
                    onItemClicked = onItemClicked,
                    openBottomDialog = openBottomDialog
                )
            }
        }
    }
}

@Composable
private fun SearchListItem(
    node: CategoriesNodeUiModel,
    isAlwaysActive: Boolean,
    onSelectionToggle: (CategoriesNodeUiModel) -> Unit,
    onItemClicked: ((CategoriesNodeUiModel) -> Unit)?,
    openBottomDialog: ((CategoriesNodeUiModel) -> Unit)?
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
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.alpha(0.9f)
            ) {
                CompositionLocalProvider(LocalContentColor provides OnSurfaceVariant) {
                    if (node.type != SubjectType.SUBJECT) {
                        IconButton(onClick = { openBottomDialog?.invoke(node) }) {
                            Icon(
                                ArxivIcons.Info,
                                contentDescription = null,
                            )
                        }
                    }
                    if (childCount > 0) {
                        Text(
                            node.childCount.toString(),
                            fontWeight = FontWeight.Medium,
                            fontSize = SubjectsDimens.countTextSize
                        )
                        Spacer(Modifier.width(Dimens.spacingTiny))
                        Icon(
                            ArxivIcons.SmallArrowRight,
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
            headlineColor = OnSurface,
            containerColor = if (isAlwaysActive)
                MaterialTheme.colorScheme.background
            else MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun rememberPagerState(
    pagerState: SubjectsPagerState
): SubjectsPagerState {
    return rememberSaveable(saver = SubjectsPagerState.Saver) {
        pagerState
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(data: BottomSheetData, onDismiss: () -> Unit) {
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
private fun ModalSheetContent(data: BottomSheetData) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingNormal),
        modifier = androidx.compose.ui.Modifier.padding(SubjectsDimens.bsContentPaddings)
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
                modifier = Modifier.padding(top = SubjectsDimens.bsSmallText)
            )
        }
        Text(
            text = data.desc,
            style = OnboardingSmallTheme,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SearchPagerExpanded(
    title: String, onBackPressed: () -> Unit
) {

    val density = LocalDensity.current
    val insects = SearchBarDefaults.windowInsets

    BackHandler(onBack = onBackPressed)
    Surface(
        modifier = Modifier
            .zIndex(4f)
            .padding(
                top = insects
                    .asPaddingValues(density)
                    .calculateTopPadding() + SubjectsDimens.searchBarVerticalPadding
            ),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(SearchBarDefaults.InputFieldHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    ArxivIcons.ArrowBack,
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
                    end = SubjectsDimens.searchTitleTextPaddingEnd
                )
            )
        }
    }
}


@Preview(
    widthDp = 320, heightDp = 240, backgroundColor = 0xFFFFF4F3
)
@Composable
private fun SearchItemPreview() {
    val data = BottomSheetData("Biomoleculsdafsfdasdfsafdsaes", "q-bio.BM", "How are you doing?")

    ArxivTheme {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
//            ModalSheetContent(data = data)
            SearchPagerExpanded("Physasdfasfadsfsdafasdasdfics") {}
//            SearchListItem(
//                node = CategoriesNodeUiModel(
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