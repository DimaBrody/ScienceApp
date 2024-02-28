package com.brody.arxiv.shared.subjects.data.di

import android.content.Context
import com.brody.arxiv.shared.subjects.data.datastore.SubjectsPreferencesDataStore
import com.brody.arxiv.shared.subjects.data.datastore.dataStore
import com.brody.arxiv.shared.subjects.data.repository.SubjectsRepositoryImpl
import com.brody.arxiv.shared.subjects.data.source.SubjectsDataSource
import com.brody.arxiv.shared.subjects.data.source.SubjectsDataSourceImpl
import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SubjectsHiltModule {

    @Binds
    fun bindSubjectsRepository(subjectsRepositoryImpl: SubjectsRepositoryImpl): SubjectsRepository

    @Binds
    fun bindSubjectsDataSource(subjectsDataSourceImpl: SubjectsDataSourceImpl): SubjectsDataSource

    companion object {
//        @Provides
//        fun provideSubscriptionApi(retrofit: Retrofit): SubscriptionApi = retrofit.create(SubscriptionApi::class.java)

        @Provides
        @Singleton
        fun providePreferenceDataStore(
            @ApplicationContext context: Context
        ): SubjectsPreferencesDataStore {
            return SubjectsPreferencesDataStore(context.dataStore)
        }

        @Provides
        @Singleton
        fun provideJson(): Json = Json
    }


}