package com.brody.arxiv.shared.subjects.data.source

import android.content.Context
import com.brody.arxiv.shared.subjects.data.datastore.SubjectsPreferencesDataStore
import com.brody.arxiv.shared.subjects.models.domain.UserSubjects
import com.brody.arxiv.shared.subjects.models.data.ArxivModel
import com.brody.arxiv.shared.subjects.models.data.ArxivSubjectsJsonModel
import com.brody.arxiv.shared.subjects.models.domain.SubjectDescriptions
import com.brody.arxiv.core.threading.ArxivDispatchers.IO
import com.brody.arxiv.core.threading.Dispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

const val SUBJECTS_PATH = "subjects_min.json"
const val DESCRIPTIONS_PATH = "descriptions.json"

internal class SubjectsDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subjectsPreferencesDataSource: SubjectsPreferencesDataStore,
    private val json: Json,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : SubjectsDataSource {
    override val userSubjects: Flow<UserSubjects>
        get() = subjectsPreferencesDataSource.subjectsList.map { UserSubjects(it) }

    override val allSubjects: Flow<List<ArxivModel>>
        get() = flow { emit(loadSubjectsFromAssets()) }.flowOn(ioDispatcher)

    private fun loadSubjectsFromAssets(): List<ArxivModel> {
        val jsonString = context.assets.open(SUBJECTS_PATH).bufferedReader().use { it.readText() }
        return json.decodeFromString<ArxivSubjectsJsonModel>(jsonString).subjects
    }

    override val subjectDescriptions: Flow<SubjectDescriptions>
        get() = flow { emit(loadSubjectDescriptions()) }.flowOn(ioDispatcher)

    private fun loadSubjectDescriptions(): SubjectDescriptions {
        val jsonString =
            context.assets.open(DESCRIPTIONS_PATH).bufferedReader().use { it.readText() }
        return parseSubjectsJson(jsonString)
    }

    private fun parseSubjectsJson(jsonString: String): HashMap<String, String> {
        val json = Json { ignoreUnknownKeys = true }

        val jsonElement = json.parseToJsonElement(jsonString)
        return json.decodeFromJsonElement<HashMap<String, String>>(jsonElement)
    }
}