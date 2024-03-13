package com.brody.arxiv.shared.papers.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.brody.arxiv.shared.papers.models.data.PaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflinePapersDao {
    @Query(value = "SELECT * FROM offline_papers")
    fun getPaperEntities(): Flow<List<PaperEntity>>

    @Query("DELETE FROM offline_papers")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<PaperEntity>)

    @Transaction
    suspend fun replaceAll(entities: List<PaperEntity>) {
        deleteAll()
        insertAll(entities)
    }
}