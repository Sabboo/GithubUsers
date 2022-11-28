package com.saber.githubusers.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("login") val name: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("avatar_url") val avatar: String?,
    @SerializedName("followers ") val followers: Int?,
    @SerializedName("following") val following: Int?,
    var note: String? = null
)