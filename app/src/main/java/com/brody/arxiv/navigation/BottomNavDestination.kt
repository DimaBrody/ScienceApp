package com.brody.arxiv.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.brody.arxiv.core.common.actions.MainToolbarAction
import com.brody.arxiv.features.papers.presentation.R as papersR
import com.brody.arxiv.features.explore.presentation.R as exploreR
import com.brody.arxiv.features.saved.presentation.R as savedR
import com.brody.arxiv.features.settings.presentation.R as settingsR
import com.brody.arxiv.designsystem.ui.icons.ArxivIcons

typealias ToolbarSecondaryButtons = List<Pair<ImageVector, MainToolbarAction>>

enum class BottomNavDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleTextId: Int,
    val toolbarSecondaryButtons: ToolbarSecondaryButtons? = null,
) {
    PAPERS(
        selectedIcon = ArxivIcons.PapersActive,
        unselectedIcon = ArxivIcons.PapersInactive,
        titleTextId = papersR.string.feature_papers_title,
        toolbarSecondaryButtons = listOf(ArxivIcons.Filter to MainToolbarAction.FILTERS),
    ),
    EXPLORE(
        selectedIcon = ArxivIcons.Explore,
        unselectedIcon = ArxivIcons.Explore,
        titleTextId = exploreR.string.feature_explore_title,
        toolbarSecondaryButtons = listOf(ArxivIcons.Search to MainToolbarAction.SEARCH)
    ),
    SAVED(
        selectedIcon = ArxivIcons.SavedActive,
        unselectedIcon = ArxivIcons.SavedInactive,
        titleTextId = savedR.string.feature_saved_title,
        toolbarSecondaryButtons = listOf(ArxivIcons.SavedHistory to MainToolbarAction.HISTORY)
    ),
    SETTINGS(
        selectedIcon = ArxivIcons.SettingsActive,
        unselectedIcon = ArxivIcons.SettingsInactive,
        titleTextId = settingsR.string.feature_settings_title,
    )
}