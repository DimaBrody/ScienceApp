package com.brody.arxiv.features.onboarding.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.brody.arxiv.designsystem.dimens.Dimens
import com.brody.arxiv.designsystem.theme.OnboardingUnderSearchTheme
import com.brody.arxiv.designsystem.ui.button.DefaultTextButton
import com.brody.arxiv.designsystem.ui.button.PrimaryButton
import com.brody.arxiv.features.onboarding.R
import com.brody.arxiv.shared.search.presentation.ui.ExpandableSearchBar
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData
import com.brody.arxiv.shared.subjects.models.presentation.SubjectsRequest
import com.brody.arxiv.shared.subjects.presentation.ui.chips.SubjectsChipsContent
import kotlinx.collections.immutable.ImmutableList


@Composable
fun SubjectsPage(
    onSubjectChipsUpdated: (List<SubjectChipData>) -> Unit,
    onCompleteClick: () -> Unit,
    onGetCurrentChips: () -> List<Int>,
    updatedSubjects: () -> MutableState<ImmutableList<SubjectChipData>?>,
    onBackClicked: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        val completeText = stringResource(R.string.complete)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExpandableSearchBar(onGetCurrentChips)

            Text(
                text = stringResource(id = R.string.select_subjects_desc),
                modifier = Modifier.padding(Dimens.spacingNormal),
                textAlign = TextAlign.Center,
                style = OnboardingUnderSearchTheme
            )
        }

        SubjectsChipsContent(
            subjectsRequest = SubjectsRequest.Onboarding,
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .padding(Dimens.spacingSmall),
            onSubjectChipsUpdated = onSubjectChipsUpdated
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingNormal),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            DefaultTextButton(onClick = onBackClicked, textId = R.string.back)
            PrimaryButton(
                onClick = onCompleteClick,
                text = completeText,
                enabled = !updatedSubjects().value.isNullOrEmpty()
            )
        }
    }
}
