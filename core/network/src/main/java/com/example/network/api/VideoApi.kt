package com.example.network.api

import com.example.network.model.PopularVideoListResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface VideoApi {

    @GET("videos/popular")
    suspend fun getPopularVideos(
        @Header("Authorization") apiKey: String,
        @Query("per_page") perPage: Int,
    ): PopularVideoListResponse
}