package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.data.model.toDomain
import com.example.data.paging.MediaPagingSource
import com.example.domain.model.MediaItem
import com.example.domain.model.Video
import com.example.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiKey

class VideoRepositoryImpl @Inject constructor(
    private val remoteDataSource: MediaRemoteDataSource,
    @ApiKey private val apiKey: String
) : VideoRepository {

    override suspend fun getPopularVideos(perPage: Int): Result<List<Video>> {
        return try {
            val response = remoteDataSource.getPopularVideos(
                apiKey = apiKey,
                page = 1,
                perPage = perPage
            )
            Result.success(response.videos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMediaPagingData(): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                MediaPagingSource(
                    remoteDataSource = remoteDataSource,
                    apiKey = apiKey
                )
            }
        ).flow
    }
}
