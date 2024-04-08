package com.brody.arxiv.shared.settings.ai.models.data

import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.EnumMap

@Serializable
data class ModelKeys(
    @SerialName("keys")
    val keys: Map<LanguageModel, String?>
) {
    companion object {
        fun createDefault() = ModelKeys(EnumMap(LanguageModel::class.java))
    }
}

fun ModelKeys.toDomainModel() = LanguageModelKeys(
    keys = keys
)