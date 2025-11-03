package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.MediaItem
import com.example.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    suspend fun getPopularVideos(perPage: Int): Result<List<Video>>
    fun getMediaPagingData(): Flow<PagingData<MediaItem>>
}
