package com.brody.arxiv.features.onboarding.presentation.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal class OnboardingViewModel : ViewModel() {

    private var updatedSubjects = mutableStateOf<ImmutableList<SubjectChipData>?>(null)

    fun onSelectedSubjectsUpdated(subjects: List<SubjectChipData>) {
        this.updatedSubjects.value = subjects.toImmutableList()
    }

    val onCurrentSelectedSubjects: () -> List<Int> =
        { updatedSubjects.value?.map { it.link.createLinkFromBits() } ?: listOf() }

    val onUpdatedSubjects: () -> MutableState<ImmutableList<SubjectChipData>?> =
        { updatedSubjects }

}
