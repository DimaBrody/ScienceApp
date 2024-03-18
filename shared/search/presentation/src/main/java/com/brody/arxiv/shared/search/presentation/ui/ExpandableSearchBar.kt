package com.brody.arxiv.shared.search.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brody.arxiv.designsystem.animations.animateDpAsStateNoLabel
import com.brody.arxiv.designsystem.dimens.Dimens.zeroDp
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons
import com.brody.arxiv.shared.search.presentation.R
import com.brody.arxiv.shared.search.presentation.dimens.SearchDimens.ActiveSearchPadding
import com.brody.arxiv.shared.search.presentation.dimens.SearchDimens.InactiveSearchPadding
import com.brody.arxiv.shared.subjects.presentation.ui.list.HandlePagerExpanded
import com.brody.arxiv.shared.subjects.presentation.ui.list.SubjectsList


@Composable
fun ExpandableSearchBar(
    onGetCurrentChips: (() -> List<Int>)?
) {

    Box {
        HandlePagerExpanded()
        ExpandableSearchBarInternal(
            onGetCurrentChips = onGetCurrentChips
        )
    }
}

@Composable
private fun ExpandableSearchBarInternal(
    onGetCurrentChips: (() -> List<Int>)?,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchText = stringResource(R.string.search)
    val searchIconText = stringResource(R.string.search_icon)
    val closeIconText = stringResource(R.string.close_icon)

    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val animatedPadding by animateDpAsStateNoLabel(
        targetValue = if (isSearchActive) InactiveSearchPadding else ActiveSearchPadding,
    )

    SearchBar(
        query = searchQuery,
        onQueryChange = viewModel::onSearchQueryChange,
        onSearch = {
            viewModel.updateActive(false)
        },
        active = isSearchActive,
        onActiveChange = { isActive ->
            viewModel.updateActive(isActive)
        },
//        enabled = viewModel.pagerExpandedTitle.isNullOrEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = animatedPadding, end = animatedPadding, top = animatedPadding / 2
            ),
        placeholder = { Text(searchText) },
        leadingIcon = {
            Icon(imageVector = ArxivIcons.Search, contentDescription = searchIconText)
        },
        trailingIcon = {
            if (isSearchActive) {
                Icon(
                    modifier = Modifier.clickable(onClick = viewModel::handleSearchClose),
                    imageVector = ArxivIcons.Close,
                    contentDescription = closeIconText
                )
            }
        },
        tonalElevation = zeroDp,
        colors = getSearchBarColors()
    ) {
        SubjectsList(
            isSearchActive = isSearchActive,
            searchFlow = viewModel.onSearchStateFlow(),
            onGetCurrentChips = onGetCurrentChips
        )
    }
}
