package com.brody.arxiv.shared.subjects.domain.repository

import com.brody.arxiv.shared.subjects.models.domain.SubjectDescriptions
import com.brody.arxiv.shared.subjects.models.domain.SubjectNames
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import com.brody.arxiv.shared.subjects.models.domain.UserSubjects
import kotlinx.coroutines.flow.Flow


interface SubjectsRepository {
    val userSubjects: Flow<UserSubjects>
    val allSubjects: Flow<SubjectsHierarchy>
    val subjectDescriptions: Flow<SubjectDescriptions>
    val subjectNames: Flow<SubjectNames>

    val allUserSubjects: Flow<SubjectsHierarchy>

    suspend fun updateUserSubjects(subjects: List<Int>)
    suspend fun setupSubject(subject: Int, isSelected: Boolean)
}