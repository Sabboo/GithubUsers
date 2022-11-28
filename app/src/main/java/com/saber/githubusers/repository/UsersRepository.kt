package com.saber.githubusers.repository

import androidx.paging.PagingData
import com.saber.githubusers.data.Result
import com.saber.githubusers.data.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun users(
        since: Int
    ): Flow<PagingData<User>>

    fun userDetails(userName: String): Flow<Result<User>>

    suspend fun updateUserNote(user: User)

    fun searchUsers(query: String): Flow<PagingData<User>>
}