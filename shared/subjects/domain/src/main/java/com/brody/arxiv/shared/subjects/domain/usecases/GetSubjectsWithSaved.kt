package com.brody.arxiv.shared.subjects.domain.usecases

import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class GetSubjectsWithSaved @Inject constructor(
    private val subjectsRepository: SubjectsRepository
) {
    operator fun invoke() = subjectsRepository.allUserSubjects
}