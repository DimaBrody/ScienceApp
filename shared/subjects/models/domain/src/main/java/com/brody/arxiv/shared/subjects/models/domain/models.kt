package com.brody.arxiv.shared.subjects.models.domain

import android.util.Log
import com.brody.arxiv.core.common.models.LinkBits

typealias SubjectDescriptions = HashMap<String, String>

typealias SubjectNames = HashMap<String, String>

const val NODE_NOT_FOUND_TEXT = "Node not found in subjects!"

class SubjectsHierarchy : HashMap<Int, CategoriesNode>() {

    fun updateWithIntLinks(links: List<Int>?): SubjectsHierarchy {
        clearAllSelected()

        links?.forEach { link ->
            val item = intLinkToNode(link)
            item.setSelected(true)
        }
        return this
    }

    fun updateSelected(link: Int, isSelected: Boolean): List<Int> {
        val item = intLinkToNode(link)
        item.setSelected(isSelected)

        return getSelectedLinkIds()
    }

    fun getSelectedLinkIds(): List<Int> {
        return values.flatMap(::getSelectedNodes).map { it.createIntLink() }
    }

    // First - selectedIds, second - excludedIds
    fun getSelectedIds(
        excludedIds: List<Int>? = null, isSelectSubjects: Boolean = false
    ): Pair<List<String>, List<String>> {

        var selectedNodes = values.flatMap(::getSelectedNodes)
        val excludedStringIds = mutableListOf<String>()

        excludedIds?.let { ids ->
            excludedStringIds.addAll(selectedNodes.filter { it.createIntLink() in ids }
                .map { it.id })
            selectedNodes = selectedNodes.filter { it.createIntLink() !in ids }
        }

//        if (isSelectSubjects) return selectedNodes.map { it.id }


        val mutableNodes = mutableListOf<CategoriesNode>()

        for (node in selectedNodes) {
            if (node.type == SubjectType.SUBJECT) {
                mutableNodes.addAll(node.childrenNodes?.values ?: emptyList())
            } else mutableNodes.add(node)
        }
        return Pair(mutableNodes.map { it.id }, excludedStringIds)
    }

    private fun getSelectedNodes(node: CategoriesNode): List<CategoriesNode> {
        val ids = mutableListOf<CategoriesNode>()

        node.childrenNodes?.values?.forEach {
            if (it.isSelected == SelectedType.SELECTED) ids.add(it)
            else ids.addAll(getSelectedNodes(it))
        }

        return ids
    }

    private fun intLinkToNode(link: Int): CategoriesNode {
        val bits = LinkBits.extractLinkBits(link)
        return findNodeByBits(bits) ?: throw NullPointerException(NODE_NOT_FOUND_TEXT)
    }

    private fun findNodeByBits(bits: LinkBits): CategoriesNode? {
        // Directly access the subject node using subjectBits
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

    fun createLinkById(id: String): Int {
        val node = findNodeById(id) ?: throw IllegalArgumentException("Node with ID $id not found")
        return node.createIntLink()
    }

    private fun findNodeById(id: String, currentNode: CategoriesNode? = null): CategoriesNode? {
        val searchNode = currentNode ?: this.values.find { it.id == id }
        if (searchNode != null) return searchNode

        this.values.forEach { node ->
            node.childrenNodes?.values?.forEach { childNode ->
                val result = findNodeById(id, childNode)
                if (result != null) return result
            }
        }
        return null
    }

    private fun clearAllSelected() {
        this.values.forEach { node ->
            clearNodeSelected(node)
        }
    }

    private fun clearNodeSelected(node: CategoriesNode) {
        node.setSelected(false)
        node.childrenNodes?.values?.forEach { childNode ->
            clearNodeSelected(childNode)
        }
    }
}