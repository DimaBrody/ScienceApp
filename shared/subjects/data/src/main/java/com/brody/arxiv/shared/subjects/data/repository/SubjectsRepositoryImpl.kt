package com.brody.arxiv.shared.subjects.data.repository

import android.util.Log
import com.brody.arxiv.core.threading.ApplicationScope
import com.brody.arxiv.shared.subjects.data.datastore.SubjectsPreferencesDataStore
import com.brody.arxiv.shared.subjects.data.source.SubjectsDataSource
import com.brody.arxiv.shared.subjects.models.domain.UserSubjects
import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import com.brody.arxiv.shared.subjects.models.data.createCategoriesTree
import com.brody.arxiv.shared.subjects.models.domain.SubjectDescriptions
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

internal class SubjectsRepositoryImpl @Inject constructor(
    private val subjectsDataSource: SubjectsDataSource,
    private val dataStore: SubjectsPreferencesDataStore,
    @ApplicationScope private val scope: CoroutineScope
) : SubjectsRepository {

    override val userSubjects: Flow<UserSubjects>
        get() = subjectsDataSource.userSubjects


    override suspend fun updateUserSubjects(subjects: List<Int>) {
        dataStore.updateSubjectsList(subjects)
    }

    override suspend fun setupSubject(subject: Int, isSelected: Boolean) {
        dataStore.setupSubject(subject, isSelected)
    }

    override val subjectDescriptions: Flow<SubjectDescriptions>
        get() = subjectsDataSource.subjectDescriptions.shareIn(
            scope, SharingStarted.WhileSubscribed(), replay = 1
        )

    override val subjectNames: Flow<SubjectDescriptions>
        get() = subjectsDataSource.subjectNames.shareIn(
            scope, SharingStarted.WhileSubscribed(), replay = 1
        )

    override val allUserSubjects: Flow<SubjectsHierarchy> = userSubjects.combine(
        allSubjects
    ) { userSubjects, allSubjects ->
        val currentTime = System.currentTimeMillis()
        val items = allSubjects.updateWithIntLinks(userSubjects.items)
        items
    }.shareIn(scope, SharingStarted.Lazily, replay = 1)

    override val allSubjects: Flow<SubjectsHierarchy>
        get() = subjectsDataSource.allSubjects.map(::createCategoriesTree)
            .shareIn(scope, SharingStarted.Lazily, replay = 1)


}