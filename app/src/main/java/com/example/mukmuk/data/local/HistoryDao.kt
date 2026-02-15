package com.example.mukmuk.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mukmuk.data.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntry>>

    @Insert
    suspend fun insert(entry: HistoryEntry)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
