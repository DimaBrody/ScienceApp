package com.brody.arxiv.shared.subjects.models.presentation

import androidx.compose.runtime.Stable
import androidx.compose.ui.state.ToggleableState
import com.brody.arxiv.shared.subjects.models.domain.CategoriesNode
import com.brody.arxiv.shared.subjects.models.domain.SelectedType
import com.brody.arxiv.shared.subjects.models.domain.SubjectType

@Stable
data class CategoriesNodeUiModel(
    val id: String,
    val link: Int,
    val idBit: Int,
    val name: String,
    val type: SubjectType,
    val selectedType: SelectedType,
    val childrenNodes: Map<Int, CategoriesNodeUiModel>?
) {
    val childCount: Int
        get() = childrenNodes?.values?.let { children ->
            children.size + children.sumOf { it.childCount }
        } ?: 0

    val isSelected: Boolean
        get() = selectedType == SelectedType.SELECTED

    val toggleableState: ToggleableState = when {
        isSelected -> ToggleableState.On
        isFullUnselected -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    private val isFullUnselected: Boolean
        get() = childrenNodes?.values?.all { it.selectedType == SelectedType.UNSELECTED } ?: true
}

internal fun CategoriesNode.checkNameContains(text: String?): Boolean =
    text?.let { ("$name $id").lowercase().contains(it.lowercase()) } ?: false