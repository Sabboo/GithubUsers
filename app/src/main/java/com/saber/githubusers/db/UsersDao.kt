package com.saber.githubusers.db

import androidx.paging.PagingSource
import androidx.room.*
import com.saber.githubusers.data.User

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Transaction
    suspend fun insertOrUpdate(user: User) {
        val existingUser = getUserById(user.id)
        if (existingUser == null)
            insert(user)
        else update(user.apply { note = existingUser.note })
    }

    @Query("SELECT * FROM User WHERE user.id LIKE :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM User")
    fun getUsers(): PagingSource<Int, User>

    @Query("SELECT * FROM User")
    suspend fun getCachedUsers(): List<User>

    @Query("SELECT * FROM User WHERE user.name LIKE :name")
    suspend fun getUserDetails(name: String): User

    @Query(
        "SELECT * FROM User WHERE user.name LIKE '%' || :query || '%' OR" +
                " user.note LIKE '%' || :query || '%'"
    )
    fun searchUsers(query: String): PagingSource<Int, User>

    @Update
    suspend fun updateUsers(vararg users: User)
}
