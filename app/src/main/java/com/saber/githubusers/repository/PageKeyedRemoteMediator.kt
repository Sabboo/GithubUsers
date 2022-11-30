package com.saber.githubusers.repository

import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.saber.githubusers.api.GithubAPI
import com.saber.githubusers.db.UsersDb
import com.saber.githubusers.db.UsersDao
import com.saber.githubusers.db.UsersRemoteKeyDao
import com.saber.githubusers.data.UserRemoteKey
import com.saber.githubusers.data.User


class PageKeyedRemoteMediator(
    private val db: UsersDb,
    private val githubAPI: GithubAPI,
    private var offset: Int
) : RemoteMediator<Int, User>() {

    private val usersDao: UsersDao = db.users()
    private val remoteKeyDao: UsersRemoteKeyDao = db.remoteKeys()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, User>
    ): MediatorResult {
        try {
            // First we define our loadKey (The next chunk of data we should fetch)
            // Basically we are getting the last inserted remote_key (in terms of time of insertion)
            // And then we use it to get nextPageKey
            val loadKey = when (loadType) {
                REFRESH -> null
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    val lastRemoteKey = db.withTransaction {
                        remoteKeyDao.getLastRemoteKey()
                    }
                    if (lastRemoteKey.nextPageKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    offset++
                    lastRemoteKey.nextPageKey
                }
            }

            // Make the api call based in the given loadKey in a queued manner
            val data = githubAPI.getUsers(
                since = loadKey?.toInt() ?: 0
            )

            // Update our tables with the new reference to the loadKey
            // And add all the fetched users to user table
            if (data.isNotEmpty())
                db.withTransaction {
                    remoteKeyDao.insert(
                        UserRemoteKey(
                            (offset + 1).toString(),
                            data.last().id.toString(),
                            System.currentTimeMillis()
                        )
                    )
                    data.forEach { usersDao.insertOrUpdate(it) }
                }

            return MediatorResult.Success(endOfPaginationReached = data.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}