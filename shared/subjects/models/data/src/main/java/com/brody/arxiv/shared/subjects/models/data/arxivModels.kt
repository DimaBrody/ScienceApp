package com.brody.arxiv.shared.subjects.models.data

import com.brody.arxiv.shared.subjects.models.domain.CategoriesNode
import com.brody.arxiv.shared.subjects.models.domain.SubjectType
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArxivSubjectsJsonModel(
    @SerialName("subjects")
    val subjects: List<ArxivModel>
)

@Serializable
data class ArxivModel(
    var id: String,
    @SerialName("id_bit")
    val idBit: Int,
    var name: String,
    var type: SubjectType,
    var items: List<ArxivModel>? = null
)

fun createCategoriesTree(arxivModels: List<ArxivModel>): SubjectsHierarchy {
    val roots = SubjectsHierarchy()
    arxivModels.forEach { model ->
        roots[model.idBit] = createNodeFromModel(model)
    }
    return roots
}

private fun createNodeFromModel(
    model: ArxivModel,
    parent: CategoriesNode? = null
): CategoriesNode {
    val node = CategoriesNode(model.id, model.idBit, model.name, model.type, parent = parent)
//    println("HELLO_CREATENODE" + model.items)
    model.items?.let { modelChildren ->
        node.childrenNodes = hashMapOf()

        modelChildren.forEach { childModel ->
            node.childrenNodes?.put(childModel.idBit, createNodeFromModel(childModel, node))
        }
    }

    return node
}

