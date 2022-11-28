package com.saber.githubusers.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saber.githubusers.data.UserRemoteKey

@Dao
interface UsersRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: UserRemoteKey)

    @Query("SELECT * FROM remote_keys ORDER BY `created_at` DESC LIMIT 1")
    fun getLastRemoteKey(): UserRemoteKey
}