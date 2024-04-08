package com.brody.arxiv.shared.saved.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

private const val DATABASE_NAME = "saved_papers_database"

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
                .databaseBuilder(context, SavedPapersDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()

        @Provides
        fun providePapersDao(savedDatabase: SavedPapersDatabase): SavedPapersDao =
            savedDatabase.papersDao()
    }
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Step 1: Add the new column. SQLite sets it to '0' (false) by default.
        db.execSQL("ALTER TABLE saved_papers ADD COLUMN isSaved INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE saved_papers ADD COLUMN hasSummaries INTEGER NOT NULL DEFAULT 0")

        // Step 2: Update the column to true for all existing entries.
        db.execSQL("UPDATE saved_papers SET isSaved = 1")
    }
}