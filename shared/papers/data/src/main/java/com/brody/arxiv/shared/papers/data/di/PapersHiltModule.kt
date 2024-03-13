package com.brody.arxiv.shared.papers.data.di

import android.content.Context
import androidx.room.Room
import com.brody.arxiv.shared.papers.data.database.OfflinePapersDao
import com.brody.arxiv.shared.papers.data.database.PapersDatabase
import com.brody.arxiv.shared.papers.data.remote.api.PapersApi
import com.brody.arxiv.shared.papers.data.remote.source.PapersRemoteDataSource
import com.brody.arxiv.shared.papers.data.remote.source.PapersRemoteDataSourceImpl
import com.brody.arxiv.shared.papers.data.repository.PapersRepositoryImpl
import com.brody.arxiv.shared.papers.domain.repository.PapersRepository
import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PapersHiltModule {

    @Binds
    fun bindPapersRepository(papersRepository: PapersRepositoryImpl): PapersRepository

    @Binds
    fun bindPapersDataSource(papersRemoteDataSource: PapersRemoteDataSourceImpl): PapersRemoteDataSource




    companion object {
        @Provides
        fun providePapersApi(retrofit: Retrofit): PapersApi =
            retrofit.create(PapersApi::class.java)

        @Singleton
        @Provides
        fun providePapersDatabase(@ApplicationContext context: Context): PapersDatabase =
            Room
                .databaseBuilder(context, PapersDatabase::class.java, "papers_database")
                .build()

        @Provides
        fun providePapersDao(papersDatabase: PapersDatabase): OfflinePapersDao =
            papersDatabase.papersDao()
    }
}