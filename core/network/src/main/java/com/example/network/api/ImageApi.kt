package com.example.network.api

import com.example.network.model.CuratedPhotosResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ImageApi {

    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Header("Authorization") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): CuratedPhotosResponse
}