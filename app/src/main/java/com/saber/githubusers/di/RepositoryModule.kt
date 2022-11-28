package com.saber.githubusers.di

import android.content.Context
import com.saber.githubusers.api.GithubAPI
import com.saber.githubusers.db.UsersDb
import com.saber.githubusers.repository.UsersRepository
import com.saber.githubusers.repository.UsersRepositoryImpl
import com.saber.githubusers.work.WorkersManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @ViewModelScoped
    @Provides
    fun provideRepository(
        db: UsersDb,
        api: GithubAPI,
        workersManager: WorkersManager,
        coroutineDispatcher: CoroutineDispatcher
    ): UsersRepository = UsersRepositoryImpl(db, api, workersManager, coroutineDispatcher)

    @ViewModelScoped
    @Provides
    fun provideWorkerManager(
        @ApplicationContext context: Context
    ): WorkersManager = WorkersManager(context)

}