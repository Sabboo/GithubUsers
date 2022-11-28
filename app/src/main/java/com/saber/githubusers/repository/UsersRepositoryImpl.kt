package com.saber.githubusers.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.saber.githubusers.api.GithubAPI
import com.saber.githubusers.data.User
import com.saber.githubusers.db.UsersDb
import com.saber.githubusers.work.WorkersManager
import com.saber.githubusers.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.lang.Error
import javax.inject.Inject


class UsersRepositoryImpl @Inject constructor(
    val db: UsersDb,
    private val githubAPI: GithubAPI,
    private val workersManager: WorkersManager,
    private val ioDispatcher: CoroutineDispatcher
) : UsersRepository {
    override fun users(since: Int) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 0, initialLoadSize = 1),
        remoteMediator = PageKeyedRemoteMediator(db, githubAPI, since)
    ) {
        db.users().getUsers()
    }.flow.map {
        workersManager.manipulateData(it.toString())
        it
    }

    /** Applying Offline First Approach */
    override fun userDetails(userName: String): Flow<Result<User>> {
        return flow {
            // Get cached user details first
            val cacheResult = fetchCachedUser(userName)
            if (cacheResult.status == Result.Status.SUCCESS) {
                emit(cacheResult)
            }
            // Then start the process of fetching remote user details
            val remoteResult = fetchRemoteUser(userName)
            // When success response is received we cache user details
            if (remoteResult.status == Result.Status.SUCCESS)
                emit(Result.success(db.users().getUserDetails(userName)))
        }.flowOn(ioDispatcher)
    }

    override suspend fun updateUserNote(user: User) {
        withContext(ioDispatcher) { db.users().updateUsers(user) }
    }

    private suspend fun fetchCachedUser(userName: String): Result<User> {
        val result = db.users().getUserDetails(userName)
        return Result.success(result)
    }

    private suspend fun fetchRemoteUser(userName: String): Result<User> {
        return try {
            val remoteResponse = githubAPI.getUserDetails(userName)

            if (remoteResponse.isSuccessful && remoteResponse.body() != null) {
                db.users().insertOrUpdate(remoteResponse.body()!!)
                Result.success(remoteResponse.body())
            } else Result.error("Failed to retrieve remote user details", Error())
        } catch (exception: Exception) {
            Result.error(exception.toString(), Error(exception))
        }
    }

    override fun searchUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            pagingSourceFactory = { db.users().searchUsers(query) }
        ).flow
    }

}

const val PAGE_SIZE = 30
