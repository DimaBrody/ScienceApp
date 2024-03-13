package com.brody.arxiv.shared.saved.domain.usecases

import com.brody.arxiv.shared.saved.domain.repository.SavedPapersRepository
import com.brody.arxiv.shared.saved.models.domain.SavePaperData
import com.brody.arxiv.shared.saved.models.domain.toSaveModel
import javax.inject.Inject

class ToggleSaveItem @Inject constructor(
    private val savedPapersRepository: SavedPapersRepository
) {
    suspend operator fun invoke(id: String, item: SavePaperData? = null) {
        item?.let { savedPapersRepository.insertSaved(it) }
            ?: savedPapersRepository.removeSaved(id)
    }
}