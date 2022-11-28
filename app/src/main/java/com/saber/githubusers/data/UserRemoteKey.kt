package com.saber.githubusers.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class UserRemoteKey(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val offset: String,
    val nextPageKey: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
