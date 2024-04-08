package com.brody.arxiv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brody.arxiv.core.common.actions.MainToolbarAction
import com.brody.arxiv.core.common.properties.weak
import com.brody.arxiv.features.details.presentation.navigation.navigateToDetails
import com.brody.arxiv.features.onboarding.presentation.ui.navigation.navigateToOnboarding
import com.brody.arxiv.features.summary.presentation.navigation.navigateToSummary
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.shared.search.models.presentation.SearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@Composable
fun rememberArxivAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): ArxivAppState {
//    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        coroutineScope
    ) {
        ArxivAppState(
            navController,
            coroutineScope
        )
    }
}

@Immutable
class ArxivAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
//    val isOffline = networkMonitor.isOnline
//        .map(Boolean::not)
//        .stateIn(
//            scope = coroutineScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = false,
//        )

    val shouldShowBottomBar: Boolean
        get() = true

    var nestedViewModelStoreOwner: ViewModelStoreOwner? by weak(null)

    fun onNestedViewModelStoreOwner(): ViewModelStoreOwner? =
        nestedViewModelStoreOwner

    private val _toolbarActionsFlow: MutableSharedFlow<MainToolbarAction> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val topAppBarActionsFlow: SharedFlow<MainToolbarAction>
        get() = _toolbarActionsFlow

    private val _searchFlow: MutableStateFlow<SearchState> =
        MutableStateFlow(SearchState.Inactive)

    val searchFlow: StateFlow<SearchState>
        get() = _searchFlow

    private val _isSearchEnabledFlow = MutableStateFlow(false)

    val isSearchEnabledFlow: StateFlow<Boolean>
        get() = _isSearchEnabledFlow

    fun updateSearchEnabled(isSearchEnabled: Boolean) {
        _isSearchEnabledFlow.update { isSearchEnabled }
    }


    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    private val scrollFlow = MutableStateFlow(0)

    val topAppBarOverlapFlow: StateFlow<Boolean> =
        combine(_isSearchEnabledFlow, scrollFlow) { isSearch, scrollOffset ->
            isSearch || scrollOffset > 0
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5_000), false)

    fun onScrollListener(offset: Int) {
        scrollFlow.update { offset }
    }

    fun onConnectToSearchFlow(state: SearchState) {
        _isSearchEnabledFlow.update { state !is SearchState.Inactive }
        _searchFlow.update { state }
    }

    fun navigateToOnboarding() = navController.navigateToOnboarding()

    fun navigateToMain() = navController.navigateToMain()

    fun navigateToDetails(paperModel: SaveablePaperDataModel) =
        navController.navigateToDetails(paperModel)

    fun navigateToSummary(
        paperModel: SaveablePaperDataModel,
        isDeepLink: Boolean = false
    ) = navController.navigateToSummary(paperModel)

    fun navigateBackSummary(isDeepLink: Boolean) {
        if (isDeepLink) navigateToMain()
        else navController.navigateUp()
    }

    fun navigateToolbarAction(action: MainToolbarAction) {
        _toolbarActionsFlow.tryEmit(action)
    }
}