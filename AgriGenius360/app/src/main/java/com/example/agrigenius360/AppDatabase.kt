package com.example.agrigenius360

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [UsersEntity::class, PlantGrowthEntity::class, PlantEntity::class], version = 4, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {
    abstract fun usersDAO(): UsersDAO
    abstract fun plantGrowthDAO(): PlantGrowthDAO
    abstract fun plantDAO(): PlantDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plant_growth_records"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigrationFrom(1)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `notification_history` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `message` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `plantId` INTEGER
            )
        """.trimIndent())
    }
}