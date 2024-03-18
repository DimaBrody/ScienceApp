package com.brody.arxiv.shared.subjects.models.presentation

import com.brody.arxiv.core.common.models.LinkBits
import com.brody.arxiv.shared.subjects.models.domain.CategoriesNode

typealias SubjectsHierarchyUiModel = Map<Int, CategoriesNodeUiModel>

fun Map<Int, CategoriesNode>.toPresentationModel(filteredString: String): SubjectsHierarchyUiModel {

    return mapNotNull { (key, value) ->
        value.filterNode(filteredString)?.let {
            key to it
        }
    }.toMap()
}

fun CategoriesNode.filterNode(filteredString: String?): CategoriesNodeUiModel? {
    var childrenNodesToSet: Map<Int, CategoriesNodeUiModel>? =
        childrenNodes?.entries?.mapNotNull { entry ->
            entry.value.filterNode(filteredString)?.let {
                entry.key to it
            }
        }?.toMap()

    if (childrenNodesToSet?.isEmpty() == true) childrenNodesToSet = null

    return if (childrenNodesToSet == null) {
        if (checkNameContains(filteredString)) {
            CategoriesNodeUiModel(
                id, createIntLink(), idBit, name, type, isSelected, null
            )
        } else null
    } else CategoriesNodeUiModel(
        id, createIntLink(), idBit, name, type, isSelected, childrenNodesToSet
    )
}

fun SubjectsHierarchyUiModel.findNodeByBits(bits: LinkBits?): CategoriesNodeUiModel? {
    if (bits == null) {
        return null
    }

    val subjectNode = this[bits.subjectBits]
    if (subjectNode != null) {
        if (bits.categoryBits == 0 && bits.subCategoryBits == 0) {
            return subjectNode
        }
        // Access the category node under the subject if exists
        val categoryNode = subjectNode.childrenNodes?.get(bits.categoryBits)
        if (categoryNode != null) {
            if (bits.subCategoryBits == 0) {
                return categoryNode
            }
            // Access the sub-category node under the category if exists
            return categoryNode.childrenNodes?.get(bits.subCategoryBits)
        }
    }
    return null
}


