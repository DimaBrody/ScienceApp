package com.brody.arxiv.shared.saved.domain.usecases

import androidx.paging.PagingData
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.saved.domain.repository.SavedPapersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedPapersUseCase @Inject constructor(
    private val savedPapersRepository: SavedPapersRepository
) {
    suspend operator fun invoke(): Flow<PagingData<PaperDomainModel>> =
        savedPapersRepository.getPapers()
}