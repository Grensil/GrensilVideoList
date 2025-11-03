package com.example.data.repository

import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.data.model.toDomain
import com.example.domain.model.Video
import com.example.domain.repository.VideoRepository
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
                perPage = perPage
            )
            Result.success(response.videos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
