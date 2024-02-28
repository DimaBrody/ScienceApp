package com.brody.arxiv.shared.subjects.data.source

import com.brody.arxiv.shared.subjects.models.domain.UserSubjects
import com.brody.arxiv.shared.subjects.models.data.ArxivModel
import com.brody.arxiv.shared.subjects.models.domain.SubjectDescriptions
import kotlinx.coroutines.flow.Flow

interface SubjectsDataSource {
    val userSubjects : Flow<UserSubjects>
    val allSubjects: Flow<List<ArxivModel>>
    val subjectDescriptions: Flow<SubjectDescriptions>
}