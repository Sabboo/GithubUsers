package com.saber.githubusers

import androidx.paging.*
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.saber.githubusers.api.GithubAPI
import com.saber.githubusers.data.User
import com.saber.githubusers.db.UsersDb
import com.saber.githubusers.repository.PageKeyedRemoteMediator
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class PageKeyedRemoteMediatorTest {

    private val mockApi = mockk<GithubAPI>()

    private val mockDb = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getInstrumentation().context, UsersDb::class.java
    ).build()

    @After
    fun tearDown() {
        mockDb.clearAllTables()
        mockDb.close()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlocking {
        coEvery { mockApi.getUsers(any()) } returns MockUsersFactory.dummyUsers()

        val remoteMediator = PageKeyedRemoteMediator(
            mockDb,
            mockApi,
            0
        )
        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(pageSize = 10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue { result is RemoteMediator.MediatorResult.Success }
        assertFalse { (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached }
    }

    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runBlocking {
        coEvery { mockApi.getUsers(any()) } returns emptyList()

        val remoteMediator = PageKeyedRemoteMediator(
            mockDb,
            mockApi,
            0
        )
        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue { result is RemoteMediator.MediatorResult.Success }
        assertTrue { (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached }
    }

    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runBlocking {
        coEvery { mockApi.getUsers(any()) } throws IOException()

        val remoteMediator = PageKeyedRemoteMediator(
            mockDb,
            mockApi,
            0
        )
        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue { result is RemoteMediator.MediatorResult.Error }
    }
}
