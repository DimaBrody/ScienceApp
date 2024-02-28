package com.brody.arxiv.shared.subjects.domain.usecases

import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import javax.inject.Inject

class GetSubjectDescriptions @Inject constructor(
    private val subjectsRepository: SubjectsRepository
) {
    operator fun invoke() = subjectsRepository.subjectDescriptions
}