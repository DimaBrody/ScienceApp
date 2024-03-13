package com.brody.arxiv.shared.search.models.presentation

import com.brody.arxiv.core.common.models.LinkBits
import com.brody.arxiv.shared.subjects.models.domain.CategoriesNode
import com.brody.arxiv.shared.subjects.models.domain.SelectedType
import com.brody.arxiv.shared.subjects.models.domain.SubjectType
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import com.brody.arxiv.shared.subjects.models.presentation.SubjectChipData

typealias SearchSubjectsHierarchy = Map<Int, SearchCategoriesNode>

fun Map<Int, CategoriesNode>.toPresentationModel(filteredString: String): SearchSubjectsHierarchy {

    return mapNotNull { (key, value) ->
        value.filterNode(filteredString)?.let {
            key to it
        }
    }.toMap()
}

fun CategoriesNode.filterNode(filteredString: String?): SearchCategoriesNode? {
    var childrenNodesToSet: Map<Int, SearchCategoriesNode>? =
        childrenNodes?.entries?.mapNotNull { entry ->
            entry.value.filterNode(filteredString)?.let {
                entry.key to it
            }
        }?.toMap()

    if (childrenNodesToSet?.isEmpty() == true) childrenNodesToSet = null

    return if (childrenNodesToSet == null) {
        if (checkNameContains(filteredString)) {
            SearchCategoriesNode(
                id, createIntLink(), idBit, name, type, isSelected, null
            )
        } else null
    } else SearchCategoriesNode(
        id, createIntLink(), idBit, name, type, isSelected, childrenNodesToSet
    )
}

fun SearchSubjectsHierarchy.findNodeByBits(bits: LinkBits?): SearchCategoriesNode? {
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


