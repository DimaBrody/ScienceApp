package com.brody.arxiv.shared.subjects.domain.usecases

import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateSubjectsList @Inject constructor(
    private val subjectsRepository: SubjectsRepository
) {
    suspend operator fun invoke(
        ids: List<Int>
    ) {
        subjectsRepository.updateUserSubjects(ids)
    }
}