package com.brody.arxiv.shared.saved.data.di

import android.content.Context
import androidx.room.Room
import com.brody.arxiv.shared.saved.data.database.SavedPapersDao
import com.brody.arxiv.shared.saved.data.database.SavedPapersDatabase
import com.brody.arxiv.shared.saved.data.repository.SavedPapersRepositoryImpl
import com.brody.arxiv.shared.saved.domain.repository.SavedPapersRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SavedPapersHiltModule {

    @Binds
    fun bindSavedPapersRepository(savedRepository: SavedPapersRepositoryImpl): SavedPapersRepository

    companion object {
        @Singleton
        @Provides
        fun provideSavedPapersDatabase(@ApplicationContext context: Context): SavedPapersDatabase =
            Room
                .databaseBuilder(context, SavedPapersDatabase::class.java, "saved_papers_database")
                .build()

        @Provides
        fun providePapersDao(savedDatabase: SavedPapersDatabase): SavedPapersDao =
            savedDatabase.papersDao()
    }
}