package com.brody.arxiv.features.onboarding.presentation.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brody.arxiv.core.common.collections.equalsIgnoreOrder
import com.brody.arxiv.core.threading.ArxivDispatchers
import com.brody.arxiv.core.threading.Dispatcher
import com.brody.arxiv.shared.subjects.domain.usecases.UpdateSubjectsList
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    private val updateSubjectsList: UpdateSubjectsList,
    @Dispatcher(ArxivDispatchers.IO) private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var updatedSubjects = mutableStateOf<ImmutableList<SubjectChipData>?>(null)

    fun onSelectedSubjectsUpdated(subjects: List<SubjectChipData>) {
        if (subjects.equalsIgnoreOrder(updatedSubjects.value))
            return

        this.updatedSubjects.value = subjects.toImmutableList()
    }


    val onCurrentSelectedSubjects: () -> List<Int> =
        {
            updatedSubjects.value?.map { it.link.createLinkFromBits() } ?: listOf()
        }

    val onUpdatedSubjects: () -> MutableState<ImmutableList<SubjectChipData>?> =
        { updatedSubjects }

    fun updateActive(onFinished: () -> Unit) =
        viewModelScope.launch(coroutineDispatcher) {
            onCurrentSelectedSubjects().let {
                updateSubjectsList.invoke(it)
            }
            withContext(Dispatchers.Main) {
                onFinished()
            }
        }

}
