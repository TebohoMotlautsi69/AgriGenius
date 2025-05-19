package com.example.agrigenius360

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsersDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(users: UsersEntity)

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun findByPhone(phoneNumber: String):UsersEntity?

    @Query("""UPDATE users SET lastOtp = :otp, lastOtpExpiry = :expiry WHERE phoneNumber = :phoneNumber""")
    suspend fun storeOtp(phoneNumber: String, otp: String, expiry: Long)

    @Query("UPDATE users SET lastOtp =NULL, lastOtpExpiry = NULL WHERE phoneNumber = :phoneNumber")
    suspend fun removeOtp(phoneNumber: String)
}