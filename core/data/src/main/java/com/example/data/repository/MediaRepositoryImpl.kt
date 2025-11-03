package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.data.paging.MediaPagingSource
import com.example.domain.model.MediaItem
import com.example.domain.repository.remote.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val remoteDataSource: MediaRemoteDataSource,
    @ApiKey private val apiKey: String
) : MediaRepository {

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
