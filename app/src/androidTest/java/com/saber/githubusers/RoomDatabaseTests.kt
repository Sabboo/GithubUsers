package com.saber.githubusers

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.saber.githubusers.data.User
import com.saber.githubusers.db.UsersDao
import com.saber.githubusers.db.UsersDb
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTests {

    private lateinit var userDao: UsersDao
    private lateinit var db: UsersDb

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context, UsersDb::class.java
        ).build()
        userDao = db.users()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndRead() = runBlocking {
        val user = User(1, "Name", "Type", "Path", 10, 20)
        userDao.insert(user)
        val byID = userDao.getUserById(1)
        assertThat(byID, equalTo(user))
    }

    @Test
    @Throws(Exception::class)
    fun checkInitialCachedUsers() =
        runBlocking { assertThat(userDao.getCachedUsers().size, equalTo(0)) }

    @Test
    @Throws(Exception::class)
    fun testUpdateUserNote() = runBlocking {
        val user = User(1, "Name", "Type", "Path", 10, 20,"Note")
        userDao.insert(user)
        val newNote = "New Note"
        user.note = newNote
        userDao.update(user)
        assertThat(userDao.getUserById(1)!!.note, equalTo(newNote))
    }
}