package com.brody.arxiv.shared.subjects.domain.usecases

import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateSubjectSaved @Inject constructor(
    private val subjectsRepository: SubjectsRepository
) {
    suspend operator fun invoke(
        hierarchy: SubjectsHierarchy,
        subjectLink: Int,
        isSelected: Boolean
    ) {
        subjectsRepository.updateUserSubjects(hierarchy.updateSelected(subjectLink, isSelected))
    }
}