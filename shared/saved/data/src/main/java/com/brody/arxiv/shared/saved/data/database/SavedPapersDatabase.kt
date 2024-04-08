package com.brody.arxiv.shared.saved.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brody.arxiv.shared.saved.models.data.SavedPaperEntity

@Database(
    entities = [SavedPaperEntity::class],
    version = 2,
)
@TypeConverters(
    SavedEntryConverter::class
)
internal abstract class SavedPapersDatabase : RoomDatabase() {
    abstract fun papersDao(): SavedPapersDao
}