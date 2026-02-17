package com.example.mukmuk.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mukmuk.data.model.MenuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu ORDER BY isPreset DESC, id ASC")
    fun getAll(): Flow<List<MenuEntity>>

    @Query("SELECT * FROM menu WHERE category = :category ORDER BY isPreset DESC, id ASC")
    fun getByCategory(category: String): Flow<List<MenuEntity>>

    @Insert
    suspend fun insert(menu: MenuEntity)

    @Insert
    suspend fun insertAll(menus: List<MenuEntity>)

    @Query("DELETE FROM menu WHERE id = :id AND isPreset = 0")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM menu")
    suspend fun getCount(): Int
}
