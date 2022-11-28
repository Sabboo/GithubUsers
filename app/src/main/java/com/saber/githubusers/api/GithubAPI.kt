package com.saber.githubusers.api

import com.saber.githubusers.data.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface GithubAPI {

    @GET("users")
    suspend fun getUsers(
        @Query("since") since: Int
    ): List<User>

    @GET("users/{userName}")
    suspend fun getUserDetails(
        @Path("userName") userName: String
    ): Response<User>

    @GET
    suspend fun downloadImageWithDynamicUrl(@Url fileUrl: String?): Response<ResponseBody?>?
}