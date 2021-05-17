package com.example.githubapp.network

import com.example.githubapp.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {

    @GET("/users/{username}")
    suspend fun getUser(@Path("username")username:String):Response<User>
}