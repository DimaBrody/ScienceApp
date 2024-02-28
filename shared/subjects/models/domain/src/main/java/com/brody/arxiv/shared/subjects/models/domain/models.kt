package com.brody.arxiv.shared.subjects.models.domain

typealias SubjectDescriptions = HashMap<String, String>

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

        return getSelectedIds()
    }

    fun getSelectedIds(): List<Int> {
        return values.map(::getSelectedIdsForNode).flatten()
    }

    private fun getSelectedIdsForNode(node: CategoriesNode): List<Int> {
        val ids = mutableListOf<Int>()

        node.childrenNodes?.values?.forEach {
            if (it.isSelected == SelectedType.SELECTED) ids.add(it.createIntLink())
            else ids.addAll(getSelectedIdsForNode(it))
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

data class LinkBits(val subjectBits: Int, val categoryBits: Int, val subCategoryBits: Int) {

    fun createLinkFromBits(): Int {
        // Shift and combine the bits according to the 4-20-8 structure
        return (subjectBits shl 28) or (categoryBits shl 8) or subCategoryBits
    }

    companion object {
        fun extractLinkBits(link: Int): LinkBits {
            val subjectBits = link shr 28 and 0xF  // Extract the 4 subject bits
            val categoryBits = link shr 8 and 0xFFFFF  // Extract the 20 category bits
            val subCategoryBits = link and 0xFF  // Extract the 8 sub-category bits
            return LinkBits(subjectBits, categoryBits, subCategoryBits)
        }
    }
}
