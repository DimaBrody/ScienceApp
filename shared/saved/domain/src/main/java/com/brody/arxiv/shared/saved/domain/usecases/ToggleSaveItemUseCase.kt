package com.brody.arxiv.shared.saved.domain.usecases

import com.brody.arxiv.shared.saved.domain.repository.SavedPapersRepository
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import javax.inject.Inject

class ToggleSaveItemUseCase @Inject constructor(
    private val savedPapersRepository: SavedPapersRepository
) {
    suspend operator fun invoke(id: String, item: SaveablePaperDataModel? = null) {
        item?.let {
            val itemToInsert = if (it.hasSummaries && it.isSaved) it.copy(isSaved = false) else it
            savedPapersRepository.insertSaved(itemToInsert)
        } ?: savedPapersRepository.removeSaved(id)
    }
}