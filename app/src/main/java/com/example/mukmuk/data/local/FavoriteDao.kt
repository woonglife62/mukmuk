package com.example.mukmuk.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mukmuk.data.model.FavoriteRestaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_restaurants ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteRestaurant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteRestaurant)

    @Query("DELETE FROM favorite_restaurants WHERE restaurantName = :name")
    suspend fun deleteByName(name: String): Int

    @Query("SELECT COUNT(*) > 0 FROM favorite_restaurants WHERE restaurantName = :name")
    fun isFavorite(name: String): Flow<Boolean>
}
