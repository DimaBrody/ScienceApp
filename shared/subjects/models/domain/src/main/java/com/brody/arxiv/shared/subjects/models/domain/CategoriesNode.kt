package com.brody.arxiv.shared.subjects.models.domain

data class CategoriesNode(
    val id: String,
    val idBit: Int,
    val name: String,
    val type: SubjectType,

    var isSelected: SelectedType = SelectedType.UNSELECTED,
    var parent: CategoriesNode? = null,
    var childrenNodes: HashMap<Int, CategoriesNode>? = null
) {

    fun setSelected(isSelected: Boolean) {
        this.setSelected(SelectedType.fromBoolean(isSelected))
    }

    private fun setSelected(isSelected: SelectedType, ignoreChildren: Boolean = false) {
        this.isSelected = isSelected

        if (!ignoreChildren) {
            childrenNodes?.values?.forEach { node ->
                node.setSelected(isSelected)
            }
        }

        parent?.childrenNodes?.values?.let { neighborsNodes ->
            val selectedCount =
                neighborsNodes.sumOf { if (it.isSelected != SelectedType.UNSELECTED) 1L else 0L }
            val neighborsSize = neighborsNodes.size


            parent?.setSelected(SelectedType.fromCounts(selectedCount.toInt(), neighborsSize), true)
        }
    }

    fun createIntLink(): Int {
        var subjectBits = 0
        var categoryBits = 0
        var subCategoryBits = 0

        var currentNode: CategoriesNode? = this
        while (currentNode != null) {
            when (currentNode.type) {
                SubjectType.SUBJECT -> subjectBits = currentNode.idBit
                SubjectType.CATEGORY -> categoryBits = currentNode.idBit
                SubjectType.SUB_CATEGORY -> subCategoryBits = currentNode.idBit
            }
            currentNode = currentNode.parent
        }

        // Assuming the total bits are 32: 4 for SUBJECT, 20 for CATEGORY, and 8 for SUB_CATEGORY
        return (subjectBits shl 28) or (categoryBits shl 8) or subCategoryBits
    }

    override fun toString(): String {
        return "CategoriesNode($name: ${childrenNodes})"
    }
}

enum class SelectedType {
    SELECTED, INTERMEDIATE, UNSELECTED;

    companion object {
        fun fromBoolean(isSelected: Boolean?) = when (isSelected) {
            true -> SELECTED
            false -> UNSELECTED
            null -> INTERMEDIATE
        }

        fun fromCounts(selectedCount: Int, size: Int) = when (selectedCount) {
            size -> SELECTED
            0 -> UNSELECTED
            else -> INTERMEDIATE
        }
    }
}