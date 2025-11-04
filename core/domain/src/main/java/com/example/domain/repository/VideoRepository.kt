package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    // Local database operations - Paging (for large datasets)
    fun getSavedVideosPaged(): Flow<PagingData<Video>>

    // Local database operations - Simple (for small datasets like bookmarks)
    fun getSavedVideos(): Flow<List<Video>>
    fun getSavedVideoById(id: Long): Flow<Video?>
    suspend fun saveVideo(video: Video)
    suspend fun saveVideos(videos: List<Video>)
    suspend fun deleteVideo(video: Video)
    suspend fun deleteVideoById(id: Long)
    suspend fun isVideoSaved(id: Long): Boolean
}
