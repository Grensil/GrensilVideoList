package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.datasource.local.MediaLocalDataSource
import com.example.data.model.toDomain
import com.example.data.model.toEntity
import com.example.domain.model.Video
import com.example.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val localDataSource: MediaLocalDataSource
) : VideoRepository {



    // Local database operations - Paging (for large datasets)
    override fun getSavedVideosPaged(): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { localDataSource.getAllVideosPaged() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    // Local database operations - Simple (for small datasets like bookmarks)
    override fun getSavedVideos(): Flow<List<Video>> {
        return localDataSource.getAllVideos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSavedVideoById(id: Long): Flow<Video?> {
        return localDataSource.getVideoById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun saveVideo(video: Video) {
        localDataSource.insertVideo(video.toEntity())
    }

    override suspend fun saveVideos(videos: List<Video>) {
        localDataSource.insertVideos(videos.map { it.toEntity() })
    }

    override suspend fun deleteVideo(video: Video) {
        localDataSource.deleteVideo(video.toEntity())
    }

    override suspend fun deleteVideoById(id: Long) {
        localDataSource.deleteVideoById(id)
    }

    override suspend fun isVideoSaved(id: Long): Boolean {
        return localDataSource.getVideoByIdOnce(id) != null
    }
}
