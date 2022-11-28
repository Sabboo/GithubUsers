package com.saber.githubusers.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saber.githubusers.data.UserRemoteKey
import com.saber.githubusers.data.User


@Database(
    entities = [User::class, UserRemoteKey::class],
    version = 1, exportSchema = false
)
abstract class UsersDb : RoomDatabase() {

    abstract fun users(): UsersDao
    abstract fun remoteKeys(): UsersRemoteKeyDao

}