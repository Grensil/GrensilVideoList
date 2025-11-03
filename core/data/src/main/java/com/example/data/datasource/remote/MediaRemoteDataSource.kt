package com.example.data.datasource.remote

import com.example.network.api.ImageApi
import com.example.network.api.VideoApi
import com.example.network.model.CuratedPhotosResponse
import com.example.network.model.PopularVideoListResponse
import javax.inject.Inject

class MediaRemoteDataSource @Inject constructor(
    private val videoApi: VideoApi,
    private val imageApi: ImageApi
) {
    suspend fun getPopularVideos(
        apiKey: String,
        page: Int,
        perPage: Int
    ): PopularVideoListResponse {
        return videoApi.getPopularVideos(apiKey, page,perPage)
    }

    suspend fun getCuratedPhotos(
        apiKey: String,
        page: Int,
        perPage: Int
    ): CuratedPhotosResponse {
        return imageApi.getCuratedPhotos(apiKey, page, perPage)
    }
}
