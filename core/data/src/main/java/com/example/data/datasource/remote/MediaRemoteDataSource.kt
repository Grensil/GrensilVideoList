package com.example.data.datasource.remote

import com.example.network.api.VideoApi
import com.example.network.model.PopularVideoListResponse
import javax.inject.Inject

class MediaRemoteDataSource @Inject constructor(
    private val videoApi: VideoApi
) {
    suspend fun getPopularVideos(
        apiKey: String,
        perPage: Int
    ): PopularVideoListResponse {
        return videoApi.getPopularVideos(apiKey, perPage)
    }
}
