package com.saber.githubusers.di

import android.content.Context
import androidx.room.Room
import com.saber.githubusers.db.UsersDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): UsersDb {
        val databaseBuilder = Room.databaseBuilder(appContext, UsersDb::class.java, "users.db")
        return databaseBuilder
            .fallbackToDestructiveMigration()
            .build()
    }
}