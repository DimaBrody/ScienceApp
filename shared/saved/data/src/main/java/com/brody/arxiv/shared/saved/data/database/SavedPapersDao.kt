package com.brody.arxiv.shared.saved.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.brody.arxiv.shared.saved.models.data.SavedPaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPapersDao {
    @Query(value = "SELECT * FROM saved_papers")
    fun getSavedPaperEntities(): Flow<List<SavedPaperEntity>>

    @Query("SELECT id FROM saved_papers")
    fun getPaperIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaper(paper: SavedPaperEntity)

    // Deletes a paper by its ID.
    @Query("DELETE FROM saved_papers WHERE id = :paperId")
    suspend fun deletePaperById(paperId: String)
}