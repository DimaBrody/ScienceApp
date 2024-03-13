package com.brody.arxiv.shared.papers.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.brody.arxiv.shared.papers.models.data.PaperEntity

@Database(
    entities = [PaperEntity::class],
    version = 1
)
@TypeConverters(
    EntryConverter::class
)
internal abstract class PapersDatabase : RoomDatabase() {
    abstract fun papersDao(): OfflinePapersDao
}