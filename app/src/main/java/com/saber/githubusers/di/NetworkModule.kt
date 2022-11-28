package com.saber.githubusers.di

import android.util.Log
import com.saber.githubusers.api.GithubAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val BASE_URL = "https://api.github.com/"

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor { Log.d("API", it) }
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .dispatcher(Dispatcher().apply { maxRequests = 1 })
            .addInterceptor(logger)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL.toHttpUrlOrNull()!!)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun providesAPI(retrofit: Retrofit): GithubAPI =
        retrofit.create(GithubAPI::class.java)

}