package com.brody.arxiv.shared.subjects.models.presentation

import android.util.Log
import com.brody.arxiv.core.common.models.LinkBits
import com.brody.arxiv.shared.subjects.models.domain.CategoriesNode
import com.brody.arxiv.shared.subjects.models.domain.SelectedType
import com.brody.arxiv.shared.subjects.models.domain.SubjectType
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy

fun SubjectsHierarchy.toChipsData(
    isOnboarding: Boolean = true,
    excludedIds: Set<Int>? = null
): ChipsData {
    return if (isOnboarding) ChipsData.ChipsOnboarding(values.map {
        getSelectedIds(it, true)
    }.flatten())
    else {
        val hashMap = hashMapOf<Int, String>()
        val outputs = values.map {
            getSelectedIdsWithParent(it, hashMap, excludedIds)
        }.flatten()
        return ChipsData.ChipsFilter(outputs, hashMap)
    }
}

sealed class ChipsData(open val data: List<Pair<SubjectChipData, Boolean>>) {
    class ChipsOnboarding(override val data: List<Pair<SubjectChipData, Boolean>>) : ChipsData(data)
    class ChipsFilter(
        override val data: List<Pair<SubjectChipData, Boolean>>,
        val mapOfLinks: HashMap<Int, String>
    ) : ChipsData(data)
}

private fun getSelectedIdsWithParent(
    node: CategoriesNode, inputMap: HashMap<Int, String>, excludedIds: Set<Int>?
): List<Pair<SubjectChipData, Boolean>> {
    return node.childrenNodes?.values?.let { childNodes ->
        if (node.isSelected == SelectedType.SELECTED) {
            inputMap[LinkBits.extractLinkBits(node.createIntLink()).subjectBits] = node.name

            return childNodes.map {
                createSubjectChipItem(it, excludedIds = excludedIds)
            }
        }

        if (node.isSelected == SelectedType.INTERMEDIATE) {
            // Only selected, get current as parent and selected children as nodes
            val selectedNodes = childNodes.filter { it.isSelected == SelectedType.SELECTED }
                .map { createSubjectChipItem(it, excludedIds = excludedIds) }.toMutableList()

            val intermediateCount: Int =
                childNodes.count { it.isSelected == SelectedType.INTERMEDIATE }

            if (intermediateCount != 0) {
                // Select selected ones (before) and selected children of intermediate
                val outputs = childNodes.filter { it.isSelected == SelectedType.INTERMEDIATE }
                    .map { node ->
                        node.childrenNodes?.values?.filter { interChild -> interChild.isSelected == SelectedType.SELECTED }
                            ?.map { createSubjectChipItem(it, excludedIds = excludedIds) }.orEmpty()
                    }

                selectedNodes.addAll(outputs.flatten())
            }
            inputMap[LinkBits.extractLinkBits(node.createIntLink()).subjectBits] = node.name
            return selectedNodes
        }
        listOf()
    } ?: listOf()
}


private fun getSelectedIds(
    node: CategoriesNode, isSubjectsByDefault: Boolean = false
): List<Pair<SubjectChipData, Boolean>> {
    if (isSubjectsByDefault && node.type == SubjectType.SUBJECT && node.isSelected == SelectedType.UNSELECTED)
        return listOf(createSubjectChipItem(node, false))

    if (node.isSelected == SelectedType.SELECTED)
        return listOf(createSubjectChipItem(node))

    val ids = mutableListOf<Pair<SubjectChipData, Boolean>>()

    node.childrenNodes?.values?.forEach {
        ids.addAll(getSelectedIds(it))
    }

    return ids
}

private fun createSubjectChipItem(
    node: CategoriesNode,
    isSelected: Boolean = true,
    excludedIds: Set<Int>? = null
): Pair<SubjectChipData, Boolean> =
    SubjectChipData(
        node.name,
        LinkBits.extractLinkBits(node.createIntLink())
    ) to (excludedIds?.contains(node.createIntLink())?.not() ?: isSelected)

