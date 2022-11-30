package com.saber.githubusers


import com.saber.githubusers.api.GithubAPI
import com.saber.githubusers.data.User
import com.saber.githubusers.db.UsersDb
import com.saber.githubusers.repository.UsersRepositoryImpl
import com.saber.githubusers.work.WorkersManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertTrue


class UsersRepositoryImplTest {

    private lateinit var cut: UsersRepositoryImpl
    private val mockDB = mockk<UsersDb>()
    private val mockAPI = mockk<GithubAPI>()
    private val mockWorkersManager = mockk<WorkersManager>()

    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @Before
    fun setup() {
        cut =
            UsersRepositoryImpl(mockDB, mockAPI, mockWorkersManager, coroutinesRule.testDispatcher)
    }

    @Test
    fun `Fetch User Details - Success`() = runTest {
        val testUser = User(1, "name", "type", "path", 0, 0)
        coEvery { mockDB.users().getUserDetails(testUser.name!!) } returns testUser
        coEvery { mockAPI.getUserDetails(testUser.name!!) } returns Response.success(testUser)

        val result = cut.userDetails(testUser.name!!)
        val dbResponse = result.first()
        val apiResponse = result.last()

        coVerify { mockDB.users().getUserDetails(testUser.name!!) }
        coVerify { mockAPI.getUserDetails(testUser.name!!) }

        assertTrue { dbResponse.data == testUser }
        assertTrue { apiResponse.data == testUser }
    }

}