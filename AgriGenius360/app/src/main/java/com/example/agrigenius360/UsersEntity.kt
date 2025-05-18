package com.example.agrigenius360

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UsersEntity(
    @PrimaryKey val phoneNumber: String,
    val username: String,
    val lastOtp: String? = null,
    val lastOtpExpiry: Long? = null
)
