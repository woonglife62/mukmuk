package com.example.mukmuk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mukmuk.data.model.FavoriteRestaurant
import com.example.mukmuk.data.model.HistoryEntry
import com.example.mukmuk.data.model.MenuEntity
import com.example.mukmuk.data.model.VisitRecord

@Database(entities = [HistoryEntry::class, MenuEntity::class, FavoriteRestaurant::class, VisitRecord::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun menuDao(): MenuDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun visitRecordDao(): VisitRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `menu` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`emoji` TEXT NOT NULL, " +
                            "`category` TEXT NOT NULL, " +
                            "`color` INTEGER NOT NULL, " +
                            "`isPreset` INTEGER NOT NULL DEFAULT 0)"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `favorite_restaurants` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`restaurantName` TEXT NOT NULL, " +
                            "`timestamp` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `visit_records` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`restaurantName` TEXT NOT NULL, " +
                            "`category` TEXT NOT NULL DEFAULT '', " +
                            "`myRating` REAL NOT NULL DEFAULT 0, " +
                            "`visited` INTEGER NOT NULL DEFAULT 0, " +
                            "`visitDate` INTEGER, " +
                            "`address` TEXT NOT NULL DEFAULT '', " +
                            "`phone` TEXT NOT NULL DEFAULT '', " +
                            "`placeUrl` TEXT NOT NULL DEFAULT '', " +
                            "`latitude` REAL NOT NULL DEFAULT 0, " +
                            "`longitude` REAL NOT NULL DEFAULT 0, " +
                            "`notes` TEXT NOT NULL DEFAULT '', " +
                            "`createdAt` INTEGER NOT NULL DEFAULT 0)"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mukmuk_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
