package com.brody.arxiv.features.onboarding.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.brody.arxiv.designsystem.extensions.scrollTo
import com.brody.arxiv.designsystem.theme.ArxivTheme
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData
import com.brody.arxiv.shared.subjects.presentation.ui.list.HandleBottomSheet
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun OnboardingRoute(
    onCompleteClick: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    OnboardingScreen(
        onSubjectChipsUpdated = viewModel::onSelectedSubjectsUpdated,
        onCompleteClick = {
            viewModel.updateActive { onCompleteClick() }
        },
        onGetCurrentChips = viewModel.onCurrentSelectedSubjects,
        updatedSubjects = viewModel.onUpdatedSubjects,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    onSubjectChipsUpdated: (List<SubjectChipData>) -> Unit,
    onCompleteClick: () -> Unit,
    onGetCurrentChips: () -> List<Int>,
    updatedSubjects: () -> MutableState<ImmutableList<SubjectChipData>?>,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top,
        userScrollEnabled = false
    ) { page ->
        when (page) {
            0 -> LandingPage {
                pagerState.scrollTo(coroutineScope, 1)
            }

            1 -> SubjectsPage(
                onSubjectChipsUpdated = onSubjectChipsUpdated,
                onCompleteClick = onCompleteClick,
                onGetCurrentChips = onGetCurrentChips,
                updatedSubjects = updatedSubjects,
            ) {
                pagerState.scrollTo(coroutineScope, 0)
            }
        }
    }

    HandleBottomSheet()
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
private fun PagePreview() {
    Surface(color = Color.White) {
        ArxivTheme {
//            SecondPage(onBackClicked = {})
        }
    }

}