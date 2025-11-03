package com.example.domain.repository

import com.example.domain.model.Video

interface VideoRepository {
    suspend fun getPopularVideos(perPage: Int): Result<List<Video>>
}
