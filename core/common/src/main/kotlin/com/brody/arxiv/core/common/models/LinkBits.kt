package com.brody.arxiv.core.common.models

import java.io.Serializable

data class LinkBits(
    val subjectBits: Int, val categoryBits: Int = 0, val subCategoryBits: Int = 0
) : Serializable {

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
