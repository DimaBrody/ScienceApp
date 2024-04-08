package com.brody.arxiv.shared.summary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brody.arxiv.shared.summary.models.data.SummaryEntity
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {

    @Query("SELECT * FROM summaries_database WHERE id = :id")
    fun getSummaryById(id: String): Flow<SummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summaryEntity: SummaryEntity)

    @Update
    suspend fun updateSummary(summaryEntity: SummaryEntity)

}