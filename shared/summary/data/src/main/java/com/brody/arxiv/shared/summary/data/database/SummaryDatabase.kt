package com.brody.arxiv.shared.summary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.brody.arxiv.shared.summary.models.data.SummaryEntity

@Database(
    entities = [SummaryEntity::class],
    version = 1
)
@TypeConverters(
    SummaryEntryConverter::class
)
internal abstract class SummaryDatabase : RoomDatabase() {
    abstract fun papersDao(): SummaryDao
}