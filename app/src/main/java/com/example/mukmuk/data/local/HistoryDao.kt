package com.example.mukmuk.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mukmuk.data.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

data class MenuCount(val menuName: String, val count: Int)
data class CategoryCount(val category: String, val count: Int)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntry>>

    @Insert
    suspend fun insert(entry: HistoryEntry)

    @Query("DELETE FROM history")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT menuName, COUNT(*) as count FROM history GROUP BY menuName ORDER BY count DESC LIMIT 3")
    fun getTopMenus(): Flow<List<MenuCount>>

    @Query("SELECT category, COUNT(*) as count FROM history GROUP BY category ORDER BY count DESC")
    fun getCategoryCounts(): Flow<List<CategoryCount>>

    @Query("SELECT COUNT(*) FROM history")
    fun getTotalCount(): Flow<Int>
}
