package com.example.mukmuk.data.local

import androidx.room.*
import com.example.mukmuk.data.model.VisitRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitRecordDao {
    @Query("SELECT * FROM visit_records ORDER BY createdAt DESC")
    fun getAllRecords(): Flow<List<VisitRecord>>

    @Query("SELECT * FROM visit_records WHERE category = :category ORDER BY createdAt DESC")
    fun getByCategory(category: String): Flow<List<VisitRecord>>

    @Query("SELECT * FROM visit_records WHERE visited = 1 ORDER BY visitDate DESC")
    fun getVisited(): Flow<List<VisitRecord>>

    @Query("SELECT * FROM visit_records WHERE visited = 0 ORDER BY createdAt DESC")
    fun getNotVisited(): Flow<List<VisitRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: VisitRecord)

    @Update
    suspend fun update(record: VisitRecord)

    @Delete
    suspend fun delete(record: VisitRecord)

    @Query("DELETE FROM visit_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM visit_records WHERE restaurantName = :name LIMIT 1")
    suspend fun findByName(name: String): VisitRecord?

    @Query("SELECT COUNT(*) FROM visit_records")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM visit_records WHERE visited = 1")
    fun getVisitedCount(): Flow<Int>

    @Query("SELECT category, COUNT(*) as count FROM visit_records WHERE category != '' GROUP BY category ORDER BY count DESC")
    fun getCategoryCounts(): Flow<List<CategoryCount>>
}
