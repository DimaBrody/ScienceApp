package com.brody.arxiv.shared.subjects.models.presentation

import com.brody.arxiv.shared.subjects.models.domain.CategoriesNode
import com.brody.arxiv.shared.subjects.models.domain.SelectedType
import com.brody.arxiv.shared.subjects.models.domain.SubjectType
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy

fun SubjectsHierarchy.toChipsData(isSubjectsByDefault: Boolean): List<Pair<SubjectChipData, Boolean>> {
    return values.map { getSelectedIds(it, isSubjectsByDefault) }.flatten()
}

private fun getSelectedIds(
    node: CategoriesNode,
    isSubjectsByDefault: Boolean = false
): List<Pair<SubjectChipData, Boolean>> {
    if (isSubjectsByDefault && node.type == SubjectType.SUBJECT && node.isSelected == SelectedType.UNSELECTED)
        return listOf(SubjectChipData(node.name, node.createIntLink()) to false)

    if (node.isSelected == SelectedType.SELECTED)
        return listOf(SubjectChipData(node.name, node.createIntLink()) to true)

    val ids = mutableListOf<Pair<SubjectChipData, Boolean>>()


    node.childrenNodes?.values?.forEach {
        ids.addAll(getSelectedIds(it))
    }

    return ids
}