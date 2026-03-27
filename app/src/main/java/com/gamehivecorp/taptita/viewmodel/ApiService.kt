package com.gamehivecorp.taptita.viewmodel

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getRedditPost(
        @Url url: String,
        @QueryMap params: Map<String, String>,
        @Header("Accept-Language") language: String
    ): Response<ResponseBody>

    @GET
    suspend fun getUserInfo(
        @Url url: String,
        @QueryMap params: Map<String, String>
    ): Response<ResponseBody>
}