package com.example.agrigenius360

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UsersEntity::class], version = 1, exportSchema = false)
abstract class UsersDatabase: RoomDatabase() {
    abstract fun usersDAO(): UsersDAO
}